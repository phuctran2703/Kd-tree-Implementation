package com.example.opengl.test_spatial_structure;

public class main {
    static final int TESTCASE_COUNT = 9;
    static final String INPUT_DIR = "D:/OpenGL_Excercise2/app/src/main/java/com/example/opengl/test_spatial_structure/testcases/input";
    static final String EXPECT_DIR = "D:/OpenGL_Excercise2/app/src/main/java/com/example/opengl/test_spatial_structure/testcases/expect";
    static final String OUTPUT_DIR = "D:/OpenGL_Excercise2/app/src/main/java/com/example/opengl/test_spatial_structure/testcases/output";
    static final String TEST_PERFORMANCE_DIR = "D:/OpenGL_Excercise2/app/src/main/java/com/example/opengl/test_spatial_structure/testcases/test_performance";
    static final String LOG_DIR = "D:/OpenGL_Excercise2/app/src/main/java/com/example/opengl/test_spatial_structure/testcases";
    public static void main(String[] args) {
        for (int i = 0; i < TESTCASE_COUNT; i++) {
            Unit.UnitInput input = ReadWriteIO.readInput(INPUT_DIR, String.valueOf(i) + ".txt");

            long startTime = System.currentTimeMillis();

            Unit.UnitOutput output = Unit.functionTest(input);

            long endTime = System.currentTimeMillis();

            long elapsedTime = endTime - startTime;
            System.out.println("Executed time " + i + ": "+ elapsedTime + " ms");

            ReadWriteIO.writeText(TEST_PERFORMANCE_DIR,i + ".txt","Executed time " + i + ": "+ elapsedTime + " ms");
            ReadWriteIO.writeOutput(OUTPUT_DIR, String.valueOf(i) + ".txt", output);
            ReadWriteIO.evaluate(OUTPUT_DIR, EXPECT_DIR, LOG_DIR, TESTCASE_COUNT);
        }
    }
}
