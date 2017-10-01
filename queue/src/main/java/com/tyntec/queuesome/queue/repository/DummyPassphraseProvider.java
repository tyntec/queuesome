package com.tyntec.queuesome.queue.repository;

import java.util.Random;

public class DummyPassphraseProvider implements PassphraseProvider {

    String[] adjectives = {"Silly", "Fancy", "Quick", "Awesome", "Hyper", "Curious", "Hidden"};
    String[] animals = {"Donkey", "Cat", "Beaver", "Dog", "Monkey", "Cow", "Human", "Quokka", "Tiger", "Whale"} ;

    @Override
    public String generatePassphrase() {
        return adjectives[new Random().nextInt(adjectives.length)] + animals[new Random().nextInt(animals.length)];
    }
}
