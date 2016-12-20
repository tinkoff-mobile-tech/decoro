[![Maven Central][img version shield]][maven]

Decoro
===========

Android library designed for automatic formatting of text input by custom rules.

# Installation

Add to the `build.gradle` of your app module:
```Groovy
dependencies {
    compile 'ru.tinkoff.decoro:decoro:$latestVersion'
}
```

# Usage

### `String` formatting
In examples below the content of the String `text` will be formatted according to a _mask_.

##### Example 1. Using predefined mask
```Java
Mask mask = new MaskImpl(PredefinedSlots.RUS_PHONE_NUMBER, true);
mask.insertFront("9995554433");
System.out.println(mask.toString()); // +7 (999) 555-44-33
```

##### Using custom mask
Example 2:
```Java
Slot[] slots = new UnderscoreDigitSlotsParser().parseSlots("___ ___");
Mask mask = MaskImpl.createTerminated(slots); // 'terminated' mask
mask.insertFront("9995554433");
System.out.println(mask.toString()); // 999 555
```

Example 3:
```Java
Slot[] slots = new UnderscoreDigitSlotsParser().parseSlots("___ ___");
Mask mask = MaskImpl.createNonTeminated(slots); // 'non-terminated' mask
mask.insertFront("9995554433");
System.out.println(mask.toString()); // 999 5554433
```

### Formatting "on the fly"
In examples below all the user's text input to `EditText` will be formatted according to a mask.

##### Example 4. Using predefined mask
```Java
MaskImpl mask = MaskImpl.createTerminated(PredefinedSlots.CARD_NUMBER_USUAL);
FormatWatcher watcher = new MaskFormatWatcher(mask);
watcher.installOn(editText); // install on any TextView
```

![sample static][img sample static]

##### Masks available out of the box:
```Java
PredefinedSlots.SINGLE_SLOT                   // Any character
PredefinedSlots.RUS_PHONE_NUMBER              // Russian phone number formatted as +7 (___) ___-__-__ (digits only)
PredefinedSlots.RUS_PASSPORT                  // Series and number of russian passport formatted as ____ ______  (digits only)
PredefinedSlots.CARD_NUMBER_STANDARD          // Credit card number formatted as ____ ____ ____ ____ (digits only)
PredefinedSlots.CARD_NUMBER_STANDARD_MASKABLE // Credit card number formatted as ____ ____ ____ ____ (digits and chars 'X', 'x', '*')
PredefinedSlots.CARD_NUMBER_MAESTRO           // Credit card number formatted as ________ ____ (digits only)
PredefinedSlots.CARD_NUMBER_MAESTRO_MASKABLE  // Credit card number formatted as ________ ____ (digits and chars 'X', 'x', '*')
```

##### Example 5. Using custom mask
```Java
Slot[] slots = new UnderscoreDigitSlotsParser().parseSlots("___ ___ ___");
FormatWatcher formatWatcher = new MaskFormatWatcher(MaskImpl.createTerminated(slots));
formatWatcher.installOn(editText); // install on any TextView
```

![sample static][img sample dynamic]

# Migration

In version 1.1.0 class `FormatWatcherImpl` was renamed to `DescriptorFormatWatcher`.
Also this version introduced `MaskFormatWatcher` that offers more clean API than `DescriptorFormatWatcher`.

# References

More examples and details can be found [in our wiki][details wiki] (in Russian yet).

[maven]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22ru.tinkoff.decoro%22%20
[details wiki]: https://github.com/TinkoffCreditSystems/decoro/wiki
[img version shield]: https://img.shields.io/maven-central/v/ru.tinkoff.decoro/decoro.svg?maxAge=3600
[img sample static]: https://raw.githubusercontent.com/TinkoffCreditSystems/decoro/master/img/static1.gif
[img sample dynamic]: https://raw.githubusercontent.com/TinkoffCreditSystems/decoro/master/img/dynamic1.gif
