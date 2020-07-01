package com.example.aabir.metravv2.Utility;

/**
 * Created by abir on 4/6/2017.
 */

public class Emergency {
    private String name="";
    private String numbers="";

    public Emergency(String name, String numbers) {
        super();
        this.name = name;
        this.numbers = numbers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumbers() {
        return numbers;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }
}
