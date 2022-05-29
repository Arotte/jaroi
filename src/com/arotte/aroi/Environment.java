package com.arotte.aroi;

import java.util.HashMap;
import java.util.Map;

class Environment {
    final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    Environment() {
        this.enclosing = null;
    }

    Environment(Environment environment) {
        this.enclosing = environment;
    }

    Object get(Token name) {
        // return value of variable
        if (values.containsKey(name.lexeme))
            return values.get(name.lexeme);

        // Recursive step:
        // if variable is not present in current local scope,
        // get it from the enclosing scope
        if (enclosing != null)
            return enclosing.get(name);

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    void define(String name, Object value) {
        // define a new variable
        values.put(name, value);
    }

    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        // Recursive step:
        // if variable is not present in current scope
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }
}
