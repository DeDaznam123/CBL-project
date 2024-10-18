
/**
 * Grid.
 */
public class Grid {

    private static final int SIZE = 30;
    private static int[][] grid = new int[SIZE][SIZE];

    private final static int[][][] templateType1 = {
        {{1,1,1,1,0,0,1,1,1,1},
         {1,0,0,0,0,0,0,0,0,1},
         {1,0,1,1,0,1,1,1,0,1},
         {1,0,1,0,0,0,0,1,0,1},
         {0,0,0,0,1,1,0,0,0,0},
         {0,0,1,0,1,1,0,1,0,0},
         {1,0,1,0,0,0,0,1,0,1},
         {1,0,1,1,0,1,1,1,0,1},
         {1,0,0,0,0,0,0,0,0,1},
         {1,1,1,1,0,0,1,1,1,1}
        },
       
        {{1,1,1,1,0,0,1,1,1,1},
         {1,0,0,0,0,0,0,0,0,1},
         {1,0,1,1,0,0,1,1,0,1},
         {1,0,1,1,0,0,1,1,0,1},
         {0,0,0,0,0,0,0,0,0,0},
         {0,0,0,0,0,0,0,0,0,0},
         {1,0,1,1,0,0,1,1,0,1},
         {1,0,1,1,0,0,1,1,0,1},
         {1,0,0,0,0,0,0,0,0,1},
         {1,1,1,1,0,0,1,1,1,1}
        },

        {{1,1,1,1,0,0,1,1,1,1},
         {1,0,0,0,0,0,0,0,0,1},
         {1,0,0,0,0,0,0,0,0,1},
         {1,0,0,1,1,1,1,0,0,1},
         {0,0,0,1,1,1,1,0,0,0},
         {0,0,0,1,1,1,1,0,0,0},
         {1,0,0,1,1,1,1,0,0,1},
         {1,0,0,0,0,0,0,0,0,1},
         {1,0,0,0,0,0,0,0,0,1},
         {1,1,1,1,0,0,1,1,1,1}
        }
    };

    private final static int[][][] templateType2 = {
        {{1,1,1,1,1,1,1,1,1,1},
         {1,0,0,0,0,0,0,0,0,1},
         {1,0,1,1,1,1,1,0,0,1},
         {1,0,1,0,0,0,0,0,0,1},
         {1,0,1,0,0,0,0,0,0,0},
         {1,0,1,0,0,1,1,0,0,0},
         {1,0,1,0,0,1,1,0,0,1},
         {1,0,0,0,0,0,0,0,0,1},
         {1,0,0,0,0,0,0,0,0,1},
         {1,1,1,1,0,0,1,1,1,1}
        },

        {{1,1,1,1,1,1,1,1,1,1},
         {1,0,0,0,0,0,0,0,0,1},
         {1,0,1,1,1,0,0,0,0,1},
         {1,0,1,1,1,0,0,0,0,1},
         {1,0,1,1,0,0,0,0,0,0},
         {1,0,0,0,0,0,1,1,0,0},
         {1,0,0,0,0,1,1,1,0,1},
         {1,0,0,0,0,1,1,1,0,1},
         {1,0,0,0,0,0,0,0,0,1},
         {1,1,1,1,0,0,1,1,1,1}
        },

        {{1,1,1,1,1,1,1,1,1,1},
         {1,0,0,0,0,0,0,0,0,1},
         {1,0,0,1,1,1,1,0,0,1},
         {1,0,1,1,1,1,1,1,0,1},
         {1,0,1,1,1,1,1,1,0,0},
         {1,0,1,1,1,1,1,1,0,0},
         {1,0,1,1,1,1,1,1,0,1},
         {1,0,0,1,1,1,1,0,0,1},
         {1,0,0,0,0,0,0,0,0,1},
         {1,1,1,1,0,0,1,1,1,1}
        }
    };

    private final static int[][][] templateType3 = {
        {{1,1,1,1,1,1,1,1,1,1},
         {1,1,0,0,0,0,0,0,1,1},
         {1,0,0,0,0,0,0,0,0,1},
         {1,0,1,1,0,0,1,1,0,1},
         {0,0,0,0,0,0,0,0,0,0},
         {0,0,0,0,0,0,0,0,0,0},
         {1,0,1,1,0,0,1,1,0,1},
         {1,0,0,1,0,0,1,0,0,1},
         {1,0,0,0,0,0,0,0,0,1},
         {1,1,1,1,0,0,1,1,1,1}
        },

        {{1,1,1,1,1,1,1,1,1,1},
         {1,1,1,1,1,1,1,1,1,1},
         {1,1,1,1,1,1,1,1,1,1},
         {1,1,1,1,1,1,1,1,1,1},
         {0,0,0,0,0,0,0,0,0,0},
         {0,0,0,0,0,0,0,0,0,0},
         {1,1,1,1,0,0,1,1,1,1},
         {1,1,1,1,0,0,1,1,1,1},
         {1,1,1,1,0,0,1,1,1,1},
         {1,1,1,1,0,0,1,1,1,1}
        },

        {{1,1,1,1,1,1,1,1,1,1},
         {1,1,0,0,0,0,0,0,1,1},
         {1,0,0,0,0,0,0,0,0,1},
         {1,0,1,1,0,0,1,1,0,1},
         {0,0,1,1,0,0,1,1,0,0},
         {0,0,1,1,0,0,1,1,0,0},
         {1,0,1,1,0,0,1,1,0,1},
         {1,0,0,1,0,0,1,0,0,1},
         {1,0,0,0,0,0,0,0,0,1},
         {1,1,1,1,0,0,1,1,1,1}
        }
    };
        
    // Pixels per cell in grid.
    private static final int CELL_SIZE = 64;

    /**
     * Grid.
     */

    public static void generateGrid() {
        int randomnumber = (int) (Math.random() * 3);
        useTemplate(templateType2[1], 0, 0, 0);
        randomnumber = (int) (Math.random() * 3);
        useTemplate(templateType3[randomnumber], 1, 10, 0);
        randomnumber = (int) (Math.random() * 3);
        useTemplate(templateType2[1], 1, 20, 0);
        randomnumber = (int) (Math.random() * 3);
        useTemplate(templateType3[randomnumber], 0, 0, 10);
        randomnumber = (int) (Math.random() * 3);
        useTemplate(templateType1[randomnumber], 0, 10, 10);
        randomnumber = (int) (Math.random() * 3);
        useTemplate(templateType3[randomnumber], 2, 20, 10);
        randomnumber = (int) (Math.random() * 3);
        useTemplate(templateType2[randomnumber], 3, 0, 20);
        randomnumber = (int) (Math.random() * 3);
        useTemplate(templateType3[randomnumber], 3, 10, 20);
        randomnumber = (int) (Math.random() * 3);
        useTemplate(templateType2[randomnumber], 2, 20, 20);
        

    }

    private static void useTemplate(int[][] template, int rotation, int x, int y) {
        int[][] templateCopy = new int[template.length][template[0].length]; 
        for (int i = 0; i < template.length; i++) {
            for (int j = 0; j < template[i].length; j++) {
                templateCopy[i][j] = template[i][j];
            }
        }
        for(int i=0;i<rotation;i++) {
            templateCopy=rotate(templateCopy);
        }
        for (int i=0;i<templateCopy.length;i++) {
            for (int j=0;j<templateCopy[i].length;j++) {
                grid[x+i][y+j] = templateCopy[i][j];
            }
        }

        
        
    }

    private static int[][] rotate(int[][] template) {
        int[][] templateCopy = new int[template.length][template[0].length]; 
        for (int i = 0; i < template.length; i++) {
            for (int j = 0; j < template[i].length; j++) {
                templateCopy[i][j] = template[i][j];
            }
        }
        for(int i=0;i<template.length;i++) {
            for(int j=0;j<template[i].length;j++) {
                template[i][j] = templateCopy[j][template.length-1-i];
            }
        }
        return template;
    }

    
    /**
     * Check if a coordinate on the grid is within a wall.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return Whether the coordinates are in a wall.
     */
    public static boolean isInWall(double x, double y) {

        if (grid[(int) x / CELL_SIZE][(int) y / CELL_SIZE] == 0) {
            return false;
        }

        return true;
    }

    public static int getCellSize() {
        return CELL_SIZE;
    }

    public static int getSize() {
        return SIZE;
    }

    public static int[][] getGrid() {
        return grid;
    }
}
