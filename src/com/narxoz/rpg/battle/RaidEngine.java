package com.narxoz.rpg.battle;

import com.narxoz.rpg.bridge.Skill;
import com.narxoz.rpg.composite.CombatNode;

import java.util.Random;

public class RaidEngine {
    private Random random = new Random(1L);
    private static final int MAX_ROUNDS = 100;

    public RaidEngine setRandomSeed(long seed) {
        this.random = new Random(seed);
        return this;
    }

    public RaidResult runRaid(CombatNode teamA, CombatNode teamB, Skill teamASkill, Skill teamBSkill) {

        if (teamA == null || teamB == null || teamASkill == null || teamBSkill == null) {
            throw new IllegalArgumentException("Teams and skills must not be null.");
        }
        if (!teamA.isAlive() || !teamB.isAlive()) {
            throw new IllegalArgumentException("Both teams must have at least one alive unit.");
        }

        RaidResult result = new RaidResult();
        int rounds = 0;

        result.addLine("=== RAID START ===");
        result.addLine(teamA.getName() + " vs " + teamB.getName());
        result.addLine(teamA.getName() + " uses " + teamASkill.getSkillName() + " (" + teamASkill.getEffectName() + ")");
        result.addLine(teamB.getName() + " uses " + teamBSkill.getSkillName() + " (" + teamBSkill.getEffectName() + ")");

        while (teamA.isAlive() && teamB.isAlive() && rounds < MAX_ROUNDS) {
            rounds++;
            result.addLine("");
            result.addLine("Round " + rounds);

            if (teamA.isAlive()) {
                int beforeHpB = teamB.getHealth();
                int critBonusA = random.nextInt(100) < 10 ? 2 : 1; // 10% crit
                if (critBonusA == 2) {
                    result.addLine(teamA.getName() + " lands a CRITICAL strike!");
                }

                for (int i = 0; i < critBonusA; i++) {
                    if (teamB.isAlive()) {
                        teamASkill.cast(teamB);
                    }
                }

                int dealtA = Math.max(0, beforeHpB - teamB.getHealth());
                result.addLine(teamA.getName() + " attacks " + teamB.getName()
                        + " with " + teamASkill.getSkillName()
                        + " [" + teamASkill.getEffectName() + "]"
                        + " for " + dealtA + " total damage.");
                result.addLine(teamB.getName() + " HP left: " + teamB.getHealth());
            }


            if (teamB.isAlive()) {
                int beforeHpA = teamA.getHealth();
                int critBonusB = random.nextInt(100) < 10 ? 2 : 1;
                if (critBonusB == 2) {
                    result.addLine(teamB.getName() + " lands a CRITICAL strike!");
                }

                for (int i = 0; i < critBonusB; i++) {
                    if (teamA.isAlive()) {
                        teamBSkill.cast(teamA);
                    }
                }

                int dealtB = Math.max(0, beforeHpA - teamA.getHealth());
                result.addLine(teamB.getName() + " attacks " + teamA.getName()
                        + " with " + teamBSkill.getSkillName()
                        + " [" + teamBSkill.getEffectName() + "]"
                        + " for " + dealtB + " total damage.");
                result.addLine(teamA.getName() + " HP left: " + teamA.getHealth());
            }
        }

        result.setRounds(rounds);

        if (teamA.isAlive() && !teamB.isAlive()) {
            result.setWinner(teamA.getName());
        } else if (teamB.isAlive() && !teamA.isAlive()) {
            result.setWinner(teamB.getName());
        } else {
            result.setWinner("Draw");
        }

        result.addLine("");
        result.addLine("=== RAID END ===");
        result.addLine("Winner: " + result.getWinner());
        result.addLine("Rounds: " + result.getRounds());
        return result;
    }
}
