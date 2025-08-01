package com.studyParty.entity.group;

import lombok.Getter;

@Getter
public enum Level {
    LEVEL_1(1,100,3),
    LEVEL_2(2,250,4),
    LEVEL_3(3,600,5),
    LEVEL_4(4,1200,6),
    LEVEL_5(5,2500,7),
    LEVEL_6(6,5000,8),
    LEVEL_7(7,10000,9),
    LEVEL_8(8,20000,10),
    LEVEL_9(9,40000,10),
    LEVEL_10(10,80000,10);

    Level(int level, int needExperience, int maxPeopleNum) {
        this.level = level;
        this.needExperience = needExperience;
        this.maxPeopleNum = maxPeopleNum;
    }
    public static Level getLevel(int leave) {
        for (Level level : Level.values()) {
            if (level.level == leave) {
                return level;
            }
        }
        return LEVEL_1;
    }

    private final int needExperience;
    private final int maxPeopleNum;
    private final int level;


}
