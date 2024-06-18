/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.parser;

/**
 *
 * @author Hans
 */
public class Level {
        String map;
        String solution;
        int solutionLength;
        int uppercaseCount;
        String time;

        Level(String map, String time, String solution) {
            this.map = map;
            this.solution = solution;
            this.solutionLength = solution.length();
            this.uppercaseCount = countUppercaseLetters(solution);
            this.time = time;
        }

        private int countUppercaseLetters(String solution) {
            int count = 0;
            for (char c : solution.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    count++;
                }
            }
            return count;
        }
    }
