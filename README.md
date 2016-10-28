[![Maven Central][img version shield]][maven]

Decoro
===========

Android библиотека, предназначеная для автоматического форматирования текста по заданным правилам.

# Установка

Добавьте в build.gradle вашего проекта:
```Groovy
dependencies {
    compile "ru.tinkoff.decoro:decoro:1.1.0"
}
```

# Использование

### Одиночное форматирование
В примерах ниже содержимое строки `text` будет приведено к формату, опреленному _маской_.

##### Пример 1. Использование заранее опредленной маски
```Java
Mask mask = new MaskImpl(PredefinedSlots.RUS_PHONE_NUMBER, true);
mask.insertFront("9995554433");
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
Mask mask = MaskImpl.createTerminated(PredefinedSlots.CARD_NUMBER_USUAL);
FormatWatcher watcher = new MaskFormatWatcher(mask);
watcher.installOn(editText); // install on any TextView
```

![sample static][img sample static]

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
Slot[] slots = new UnderscoreDigitSlotsParser().parseSlots("___ ___ ___");
FormatWatcher formatWatcher = new MaskFormatWatcher(MaskImpl.createTerminated(slots));
formatWatcher.installOn(editText); // install on any TextView
```

![sample static][img sample dynamic]

# Миграция

В версии 1.1.0 был переименован класс `FormatWatcherImpl` -> `DescriptorFormatWatcher`.
Также в 1.1.0 появился класс `MaskFormatWatcher`, который предоставляет более явный и чистый API для
форматирования "на лету".

Больше примеров, возможностей и деталей можно найти [в нашей wiki][details wiki].

[maven]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22ru.tinkoff.decoro%22%20
[details wiki]: https://github.com/TinkoffCreditSystems/decoro/wiki/%D0%94%D0%B5%D1%82%D0%B0%D0%BB%D0%B8-%D1%80%D0%B5%D0%B0%D0%BB%D0%B8%D0%B7%D0%B0%D1%86%D0%B8%D0%B8,-%D0%B2%D0%BE%D0%B7%D0%BC%D0%BE%D0%B6%D0%BD%D0%BE%D1%81%D1%82%D0%B8-%D0%B8-%D0%BF%D1%80%D0%B8%D0%BC%D0%B5%D1%80%D1%8B
[img version shield]: https://img.shields.io/badge/version-1.1.0-blue.svg
[img sample static]: https://raw.githubusercontent.com/TinkoffCreditSystems/decoro/master/img/static1.gif
[img sample dynamic]: https://raw.githubusercontent.com/TinkoffCreditSystems/decoro/master/img/dynamic1.gif
