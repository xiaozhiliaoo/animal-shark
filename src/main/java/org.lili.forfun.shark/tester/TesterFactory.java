package org.lili.forfun.shark.tester;


interface TesterFactory {
    Tester makeTest(String platorm, String input, String output, boolean hasReport);
}
