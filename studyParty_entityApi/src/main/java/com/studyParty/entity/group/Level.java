package com.studyParty.entity.group;

import lombok.Getter;

@Getter
public enum Level {
    LEVEL_1(10,5),
    LEVEL_2(25,10),
    LEVEL_3(100,15),
    LEVEL_4(200,20),
    LEVEL_5(400,25),
    LEVEL_6(800,30),
    LEVEL_7(1600,35),
    LEVEL_8(3200,40),
    LEVEL_9(6400,45),
    LEVEL_10(12800,50);

    Level(int needExperience, int maxPeopleNum) {
        this.needExperience = needExperience;
        this.maxPeopleNum = maxPeopleNum;
    }
    private final int needExperience;
    private final int maxPeopleNum;


}
