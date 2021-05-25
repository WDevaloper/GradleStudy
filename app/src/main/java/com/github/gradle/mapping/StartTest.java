package com.github.gradle.mapping;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class StartTest {
    public static void main(String[] args) {
        //[ [3, 0], [3, 1], [4, 1], [4, 2], [5, 3], [5, 4] ]
        int[][] arr = {{3, 0}, {3, 1}, {4, 1}, {4, 2}, {5, 3}, {5, 4}};
        int[] order = findOrder(6, arr);
    }

    public static int[] findOrder(int num, int[][] prerequisites) {
        // 计算所有节点的入度，这里用数组代表哈希表，
        // key 是 index（即：想选的课）， value 是 inDegree[index]（即：入度）.实际开发当中，用 HashMap 比较灵活
        int[] inDegree = new int[num];
        for (int[] array : prerequisites) {
            // 想选的课 3,3,4，4,5,5
            int value = array[0];

            inDegree[value]++;
        }

        // 找出所有入度为 0 的点，加入到队列当中
        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < inDegree.length; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }

        ArrayList<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            Integer key = queue.poll();
            result.add(key);
            // 遍历所有课程

            for (int[] p : prerequisites) {
                // 改课程依赖于当前课程 key

                //[ [3, 0], [3, 1], [4, 1], [4, 2], [5, 3], [5, 4] ]
                if (key == p[1]) {
                    // 入度减一
                    inDegree[p[0]]--;
                    if (inDegree[p[0]] == 0) {
                        queue.offer(p[0]); // 加入到队列当中
                    }
                }
            }
        }

        // 数量不相等，说明存在环
        if (result.size() != num) {
            return new int[0];
        }

        int[] array = new int[num];
        int index = 0;
        for (int i : result) {
            array[index++] = i;
        }

        return array;
    }
}
