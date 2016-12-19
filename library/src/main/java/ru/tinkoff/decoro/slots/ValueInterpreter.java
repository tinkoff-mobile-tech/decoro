package ru.tinkoff.decoro.slots;

import java.io.Serializable;

/**
 * Created by a.shishkin1 on 08.12.2016.
 */

public interface ValueInterpreter extends Serializable {

    Character interpret(Character character);

}
