package com.arotte.aroi;

import java.util.HashMap;
import java.util.Map;

class Environment {
    private final Map<String, Object> values = new HashMap<>();

    Object get(Token name) {
        // return value of variable
        if (values.containsKey(name.lexeme))
            return values.get(name.lexeme);

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    void define(String name, Object value) {
        // define a new variable
        values.put(name, value);
    }
}
