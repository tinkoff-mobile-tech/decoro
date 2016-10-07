Formatting
===========

Библиотека предназначена для автоматического форматирования текста по заданным правилам.

# Примеры

### Одиночное форматирование
В примерах ниже содержимое строки `text` будет приведено к формату, опреленному _маской_.

##### Пример 1. Использование заранее опредленной маски
```Java
String text = "9995554433";
Mask mask = new MaskImpl(PredefinedSlots.RUS_PHONE_NUMBER, true);
mask.insertFront(text);
System.out.println(mask.toString()); // +7 (999) 555-44-33
```

##### Использование произвольной маски
Пример 2:
```Java
Slot[] slots = new UnderscoreDigitSlotsParser().parseSlots("___ ___");
Mask mask = MaskImpl.createTerminated(slots); // 'terminated' mask
mask.insertFront("9995554433");
System.out.println(mask.toString()); // 999 555
```

Пример 3:
```Java
Slot[] slots = new UnderscoreDigitSlotsParser().parseSlots("___ ___");
Mask mask = MaskImpl.createNonTeminated(slots); // 'non-terminated' mask
mask.insertFront("9995554433");
System.out.println(mask.toString()); // 999 5554433
```

### Форматирование "на лету"
В данных примерах весь пользовательский ввод в `EditText` будет приведен к формату, определенному _маской_.

##### Пример 4. Использование заранее опредленной маски
```Java
EditText editText = (EditText) findViewById(R.id.editCustom);
FormatWatcher watcher = new FormatWatcherImpl(MaskDescriptor.ofSlots(PredefinedSlots.CARD_NUMBER_USUAL));
watcher.installOn(editText);
```

##### Маски ввода в составе модуля:
```Java
PredefinedSlots.SINGLE_SLOT                   // Любой символ
PredefinedSlots.RUS_PHONE_NUMBER              // Телефонный номер в формате +7 (___) ___-__-__ (только цифры)
PredefinedSlots.RUS_PASSPORT                  // Серия и номер паспорта в формате ____ ______  (только цифры)
PredefinedSlots.CARD_NUMBER_USUAL             // Hомер карты в формате ____ ____ ____ ____ (только цифры)
PredefinedSlots.MASKABLE_CARD_NUMBER_USUAL    // Hомер карты в формате ____ ____ ____ ____ (цифры и символы 'X', 'x', '*')
PredefinedSlots.CARD_NUMBER_MAESTRO           // Hомер карты в формате ________ ____ (только цифры)
PredefinedSlots.MASKABLE_CARD_NUMBER_MAESTRO  // Hомер карты в формате ________ ____ (цифры и символы 'X', 'x', '*')
```

##### Пример 5. Использование произвольной маски
```Java
final EditText editText = (EditText) findViewById(R.id.editCustom);
FormatWatcher formatWatcher = new FormatWatcherImpl(
    new UnderscoreDigitSlotsParser(),
    MaskDescriptor.ofRawMask("___ ___ ___", true)
);
formatWatcher.installOn(editText);
```

# Детали реализации

В основе библиотеки лежат два понятия [_слот_](#slot-def) и [_маска_](#mask-def). Слот определяет
как будет отформатирован _один символ_. Маска же содержит список слотов и отвечает за форматирование
  _последовательности символов_.

### <a name="slot-def">Слот</a> (Slot.java)
Слот - это позиция для вставки символа. Слоты организуются в двусвязный список с помощью ссылок `Slot#nextSlot` и `Slot#prevSlot`.
Слот работает с форматированием на уровне _одного символа_, определяя возможность этого символа находиться
в данной позиции.

Существует два способа вставки символа в слот: _вставка сверху_ и _вставка слева_.
_Вставка сверху_ (вставка по умолчанию) происходит когда внешний источник (например, _маска_) устанавливает
значение в слот (однако при помощи метода Slot#setValue(Character, boolean) внешние источники могу
осуществлять _вставку слева_).
_Вставка слева_ происходит когда один слот "проталкивает" значение в следующий слот (см. ниже).
Различие между этими методами существует только для _hardcoded_ (неизменяемых) слотов и определяется _правилом_
**RULE_FORBID_LEFT_OVERWRITE** (см. ниже).

Возможность вставки символа в слот определяется _правилами вставки_ в слот и его _валидаторами_.

Для успешной вставки символа в слот:
1. Слот должен иметь правило **RULE_INPUT_MOVES_CURRENT**;
2. Символ должен удовлетворять требованиям _всех_ валидаторов
(при отсутствии валидаторов этот пункт пропускается).

#### Правила вставки
Существует два возможных правила вставки символа в слот:
1. **RULE_INPUT_MOVES_CURRENT** - новое значение сдвигает текущее значение в следующий слот. При
удалении значения из слота на его место устанавливается значение из следущего. Это поведение по умолчанию и
оно соответствует обычному режиму ввода текста в тектстовом редакторе.
2. **RULE_INPUT_MOVES_INPUT** - при попытке вставить новое значение оно не заменяет текущее значение
в слоте, а "проталкивается" в следующий. При попытке удаления значения из такого слота, ничего не проиходит.
Данное правило является необходимым для создания _hardcoded_-слотов. Такой слот имеет предустановленное заничение
и правило **RULE_INPUT_MOVES_INPUT**. Его значение нельзя изменить. По умлочанию в _hardcoded_ слот можно
вставить только его текущее значение, однако и эту вставку можно запретить (см. правило **RULE_FORBID_LEFT_OVERWRITE**).
3. **RULE_FORBID_LEFT_OVERWRITE** - правило имеет смысл только вместе с правилом **RULE_INPUT_MOVES_INPUT**.
Если в _hardcoded_ слот с этим правилом приходит значение _слева_, то оно не "перезаписывает" иекущее значение,
а "проталкивается" в следующий слот.

#### Валидаторы (SlotValidator)
С помощью валидаторов можно ограничивать множество символов, доступных для вставки в слот.
`ISlotValidator` является интерфейсом:
```Java
 public interface SlotValidator extends Serializable {
    boolean validate(final char value);
}
```

Пример простейшего валидатора - `SlotValidators.DigitValidator`. Если данный валидатор установлен на слот,
в слот можно будет вписать только цифру.
```Java
public static class DigitValidator implements Slot.SlotValidator {

    @Override
    public boolean validate(final char value) {
        return Character.isDigit(value);
    }

    // <...>
}
```

#### Теги и декоративные слоты

Помимо правил и валидаторов каждый слот может иметь набор _тегов_. Тег - это некоторый `Integer`,
ассоциированный со слтом. Теги внутри слота хранятся во множестве (`Set<Integer>`). Класс `Slot`
не содержит никакого кода по работе с тегами (кроме их установки, получения и проверки наличия).

Однако, в классе объявлена константа `Slot#TAG_DECORATION`. Слоты помеченные данным тегом
являются _декоративными_ и по особому обрабатываются _маской_ для получения _неформатированной_
строки (см. ниже).

### <a name="mask-def">Маска</a> (Mask.java и MaskImpl.java)

Маска позволяет форматировать _последовательности символов_ с помощью связного списка слотов.
Маска не занимается созданием слотов (за исключением случаев клонирования слота в нетерминированной
маске, (см. ниже)[#termination-flag]). Для получения форматированного текста, текст должен быть _вставлен_ в маску с помощью методов
`Mask#insertAt(int CharSequence)` или `Mask#insertFront(CharSequence)`. Для получения форматированной
строки необходимо вызвать метод `Mask#toString()`.

Для создания маски необходимы два параметра:
1. **Массив слотов**. Маска преобразовывет слоты в двусвязый список и хранит в в нем введенный текст.
2. **Флаг терминированности**. Определяет, возможна ли вставка символов когда все слоты заполнены.

#### <a name="termination-flag">Флаг терминированности</a>

Данный флаг определяет возможна ли вставка символов в маску, когда все слоты заполнены (см. листинги 5 и 6).
Если маска _нетерминирована_, последний слот будет бесконено копироваться, удлинняя маску.
Это позволяет делать маски "бесконечной" длины. Однако символы, вставленные "сверх нормы", форматрованы не будут.

#### Создание маски

В случае необходимости разового форматирования текста (форматирования НЕ "на лету") необходимо вручную
создать  маску. Для форматирования "на лету" (форматирование текста в `TextView` и `EditText`) ручного
 создания маски не требуется (см. ниже).

Создать маску можно тремя способами:
1. Фабричными методами `MaskImpl#createTerminated(Slot[])` и `Mask#createNonTeminated(Slot[])`;
2. С помощью комструктора `MaskImpl#MaskImpl(Slot[], boolean)`;
3. С помощью реализации интерфейса `MaskFactory`, например `MaskFactoryImpl`.

#### Параметры маски

Кроме списка слотов и флага терминированности маска обладает некоторыми настраиваемыми параметрами:
1. `forbidInputWhenFilled` - если `true`, запрещает вставку в маску новых символов. Если `false`,
при вставке в середину заполненной маски, новые символы будут "выталкивать" символы из конца маски.
По умолчанию - `false`.
2. `hideHardcodedHead` - если `true` скрывает _hardcoded_ последовательность в начале строки при
 отсутствии пользовательсткого ввода (см. пример ниже) при вызове `toString()`.
По умолчанию - `false`.
3. `showingEmptySlots` - если `true` и в маске есть незаполенные слоты, то эти слоты будут выведены
в `toString()`. **ВАЖНО**: когда данный флаг выставлен в `true`, флаг `hideHardcodedHead` игнорируется.
По умолчанию - `false`
4. `placeholder` - символ, которым заменяются пустые пустые слоты при вызове `toString()` когда
`showingEmptySlots = true`. По умолчанию `_` (нижнее подчеркивание).

##### Пример использования флага `Mask#hideHardcodedHead`
Пример 6:
```Java
Mask mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER); // +7 (___) ___-__-__
System.out.println(mask.toString()); // +7 (
```
Пример 7:
```Java
Mask mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER); // +7 (___) ___-__-__
mask.setHideHardcodedHead(true);
System.out.println(mask.toString()); // nothing
```

В листингах 7 и 8 произодится вывод пустой (без пользовательского ввода)
маски для ввода номера телефона. Данная маска имеет _hardcoded_-последовательность в начале - _"+7 ("_.
В листинге 7 будет выведена эта последовательность, т.к. `hideHardcodedHead = false`, в листинге 8
не будет выведено ничего, т.к. `hideHardcodedHead = true`.

##### Пример использования флага `Mask#showingEmptySlots` и параметра `Mask#placeholder`
Пример 8:
```Java
Mask mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER);
mask.setShowingEmptySlots(true);
mask.setPlaceholder('*');
mask.insertFront("999");
System.out.println(mask.toString()); // +7 (999) ***-**-**
```

#### Получение неформатированного ввода

Для получения неформатированной строки используется метод `Mask#getUnformattedString(boolean)`.
Данный метод возвращает строку из значений всех слотов, за исключением _декоративных_.

Пример 9: получение неформатированной строки
```Java
Mask mask = MaskImpl.createTerminated(new Slot[]{
        PredefinedSlots.digit(),                                          // слот для цифры
        PredefinedSlots.digit(),                                          // слот для цифры
        PredefinedSlots.hardcodedSlot('-').withTags(Slot.TAG_DECORATION), // декоративный hardcoded слот
        PredefinedSlots.digit(),                                          // слот для цифры
        PredefinedSlots.digit(),                                          // слот для цифры
});
mask.insertFront("1234");
System.out.println(mask.toString());                    // 12-34
System.out.println(mask.toUnformattedString());         // 1234
```
Пример 10: получение неформатированной строки
```Java
Mask mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER);
mask.insertFront("9995554433");
System.out.println(mask.toString());                    // +7 (999) 555-44-33
System.out.println(mask.toUnformattedString());         // +79995554433
```

### Парсер слотов (ISlotParser.java)

Для создания маски необходимо иметь массив слотов. Но часто приходится
создавать маску из некоторого строкового представления (_raw-строки_). Примером такого строкового представления
могут быть `+7 (___) ___-__-__` и `DD.MM.YYYY`. Чтобы иметь возможность создания маски из строки
используется интерфейс `SlotsParser`. Он позволяет сконвертировать строковое представление маски в
набор слотов.
```Java
public interface SlotsParser {
    Slot[] parseSlots(CharSequence rawMask);
}
```

В модуле предусмотрены две его реализации: `UnderscoreDigitSlotsParser` и `PhoneNumberUnderscoreSlotsParser `.

- `UnderscoreDigitSlotsParser` создает массив слотов, заменяя `_` на слоты для ввода цифры, а остальные символы на hardcoded-слоты.
- `PhoneNumberUnderscoreSlotsParser ` работает также, как и предыдущий парсер, но с одной особенностью:
все _hardcoded_ цифры в маске кроме _первой_ будут иметь флаг **RULE_FORBID_LEFT_OVERWRITE**. <br/>
Например, в маске `+369 (___) __-__-__` цифры `6` и `9` будут иметь флаг **RULE_FORBID_LEFT_OVERWRITE**.
Это необходимо для корректного форматирования, когда ввод производится в пустой `EditText`. Когда пользователь
введет цифру `3`, будет автоматически отображение строки `+369 (` и курсор будет установлен после `(`.
Если же пользователь вводит первым символом `6` или `9` - будет отображено `+369 (6` или `+369 (9`
соответственно.

### MaskDescriptor

Поскольку маска хранит введенные в нее символы и позволяет получать форматированный или неформатрованный ввод,
пересоздание маски является частой операцией. Для облегчения процесса создания маски и созранения
параметров будущей маски (слоты, форматные строки и флаги) используется класс `MaskDescriptor`.
Сущности данного класса можно использовать для задания маски в Format watcher'ах (см. ниже) или
создания маски с помощью `MaskFactoryImpl`.

`MaskDescriptor` может хранить как массив слотов для создания маски, так и raw-маску (строковое представление).
Во втором случае для создания маски из дескриптора понадобится также `SlotsParser`. Если для дескриптора
указаны оба - массив слотов и raw-маска, то приоритет имеет массив слотов (для создания маски из
такого дескриптора *не* понадобится `SlotsParser`).
Также `MaskDescriptor` хранит все _дополнительные параметры_ для маски.

#### Пример 11. Создание `MaskDescriptor`

```Java
final MaskDescriptor descriptor = MaskDescriptor.ofRawMask("+7 ___ ___-__-__")
        .withShowingEmptySlots(true)
        .withEmptySlotPalceholder('*')
        .withForbiddingInputWhenFilled(true)
        .withTermination(true)
        .withHideHardcodedHead(true)
        .withInitialValue("999");    // значение, которое будет предустановлено в маску, при ее создании
```

Кроме слотов (в виде массива или raw-строки) остальные параметры дескриптора являются _необязательными_.

### Format watcher

В ситуациях, когда необходимо форматировать текст в TextView "на лету", например во время ввода пользователем
текста в `EditText`, нам необходимо постоянно модифицировать текст внутри маски. Для этой цели используется
абстракный класс `FormatWatcher`. Класс инкапсулирует создание маски и работу с ней. Создание маски не
определено в базовом классе и дожно быть реализовано в наследниках. Format watcher _устанавливается на_
`TextView` и отслеживает изменения текста с помощью механизма `TextWatcher`.

**ВАЖНО**: не рекомендуется ипользование format watcher'ов вместе с другими TextWatcher'ами. Для
получения оповещений об изменении текста в `TextView` можно использовать `FormattedTextChangeListener`.
Он устанавливается на watcher методом `FormatWatcher#setCallback(FormattedTextChangeListener)`.

Модуль предоставляет реализацию format watcher'a - `FormatWatcherImpl`. Этот watcher позволяет
менять маску уже после того как был установлен на `TextView`. Маска создается на основе
`MaskDescriptor` и (при необходимости) `SlotsParser`.

Для корректной работы `FormatWatcherImpl` требуется указать `MaskDescriptor`. Сделать это
необходимо до установки watcher'a на `TextView` - в конструкторе `FormatWatcherImpl` или
с помощью метода `FormatWatcherImpl#changeMask(MaskDescriptor)` (с помощью этого метода можно
также сменить маску после установки watcher'a).

Если указанный `MaskDescriptor` содержит массив слотов, то его достаточно для создания маски. Если же
в декрипторе указана только raw-маска, то для создания маски потребуется предоставить `SlotsParser`.
Сделать это можно либо в конструкторе `FormatWatcherImpl`, либо с помощью метода
`FormatWatcherImpl#setSlotsParser(SlotsParser)`.

#### Привер 12. Создание watcher с raw-маской и отображением `_` на местах для ввода:
```Java
final EditText editText = (EditText) findViewById(R.id.editCustom);
FormatWatcher formatWatcher = new FormatWatcherImpl(
        new UnderscoreDigitSlotsParser(),
        MaskDescriptor.ofRawMask("___ ___ ___", true).withShowingEmptySlots(true)
);
formatWatcher.installOnAndFill(editText);
editText.setText("123456");
System.out.println(editText.getText());    // 123 456 ___
editText.getText().insert(0, "789");
System.out.println(editText.getText());    // 789 123 456
```


