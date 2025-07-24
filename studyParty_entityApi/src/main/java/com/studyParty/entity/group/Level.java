package com.studyParty.entity.group;

import lombok.Getter;

@Getter
public enum Level {
    LEVEL_1(100,3),
    LEVEL_2(250,4),
    LEVEL_3(600,5),
    LEVEL_4(1200,6),
    LEVEL_5(2500,7),
    LEVEL_6(5000,8),
    LEVEL_7(10000,9),
    LEVEL_8(20000,10),
    LEVEL_9(40000,10),
    LEVEL_10(80000,10);

    Level(int needExperience, int maxPeopleNum) {
        this.needExperience = needExperience;
        this.maxPeopleNum = maxPeopleNum;
    }
    private final int needExperience;
    private final int maxPeopleNum;


}
