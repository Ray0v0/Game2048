import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Board {
    public static final int size = 4;
    private int[][] blocks = new int[size][size];
    private int score = 0;
    public Board() {

    }
    static public int getSize() {
        return size;
    }
    public int getScore() {
        return score;
    }
    public int getBlock(int[] pos) {
        return blocks[pos[0]][pos[1]];
    }
    public int getBlock(int i, int j) {
        return blocks[i][j];
    }
    public void setBlock(int[] pos, int num) {
        blocks[pos[0]][pos[1]] = num;
    }
    public void setBlock(int i, int j, int num) {
        blocks[i][j] = num;
    }
    public int countBlocks() {
        int count = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (blocks[i][j] != 0) {
                    count++;
                }
            }
        }
        return count;
    }
    public boolean isFull() {
        boolean isFull = true;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (blocks[i][j] == 0) {
                    isFull = false;
                    break;
                }
            }
        }
        return isFull;
    }

    public boolean isAlive() {
        boolean isAlive = false;
        if (!isFull()) {
            isAlive = true;
        } else {
            for (int i = 0; i < Direction.values().length; i++) {
                isAlive = isLegal(Direction.get(i));
                if (isAlive) {
                    break;
                }
            }
        }
        return isAlive;
    }
    public void newBlock() {
        ArrayList<int[]> blankBlogPoses = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (blocks[i][j] == 0) {
                    blankBlogPoses.add(new int[]{i, j});
                }
            }
        }
        if (blankBlogPoses.size() > 0) {
            Random ran = new Random();
            int randomIndex = ran.nextInt(blankBlogPoses.size());
            int[] pos = blankBlogPoses.get(randomIndex);
            int randomNum = ran.nextInt(2) + 1;
            setBlock(pos, randomNum);
        }
    }
    public void printBoard() {
        System.out.printf("score: %d\n", score);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.printf("%d\t", blocks[i][j]);
            }
            System.out.println();
        }
    }

    private int modifyLine(ArrayList<Integer> line) {
        int sum = 0;
        for (int k = 0; k < line.size() - 1; k++) {
            int elementK = line.get(k);
            if (elementK == line.get(k + 1)) {
                sum += elementK;
                line.set(k, elementK * 2);
                line.remove(k + 1);
            }
        }
        return sum;
    }

    public boolean isLegal(Direction direction) {
        boolean ret = false;
        int[][][] pairs = Direction.get(direction);
        OUT:
        for (int i = 0; i < size; i++) {
            boolean inside = false;
            for (int j = 0; j < size; j++) {
                if (blocks[ pairs[i][j][0] ][ pairs[i][j][1] ] != 0) {
                    inside = true;
                    if (j < size - 1) {
                        if (blocks[ pairs[i][j][0] ][ pairs[i][j][1] ] == blocks[ pairs[i][j+1][0] ][ pairs[i][j+1][1] ]) {
                            ret = true;
                            break OUT;
                        }
                    }
                } else {
                    if (inside) {
                        ret = true;
                        break OUT;
                    }
                }
            }
        }
        return ret;
    }

    private int deltaScore(Direction direction) {
        int ret = 0;

        int[][] blocks = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                blocks[i][j] = this.blocks[i][j];
            }
        }

        Direction indexDirection = Direction.getOppositeDirection(direction);
        int[][][] pairs = Direction.get(indexDirection);
        for (int i = 0; i < size; i++) {
            ArrayList<Integer> line = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                if (blocks[ pairs[i][j][0] ][ pairs[i][j][1] ] != 0) {
                    line.add(blocks[ pairs[i][j][0] ][ pairs[i][j][1] ]);
                }
            }

            ret += modifyLine(line);
        }
        return ret;
    }

    public void push(Direction direction) {
        Direction indexDirection = Direction.getOppositeDirection(direction);
        int[][][] pairs = Direction.get(indexDirection);
        for (int i = 0; i < size; i++) {
            ArrayList<Integer> line = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                if (blocks[ pairs[i][j][0] ][ pairs[i][j][1] ] != 0) {
                    line.add(blocks[ pairs[i][j][0] ][ pairs[i][j][1] ]);
                }
            }

            score += modifyLine(line);

            int count = 0;
            for (int j = 0; j < size; j++) {
                if (count < line.size()) {
                    blocks[ pairs[i][j][0] ][ pairs[i][j][1] ] = line.get(count);
                } else {
                    blocks[ pairs[i][j][0] ][ pairs[i][j][1] ] = 0;
                }
                count++;
            }
        }
    }
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        double avgRound = 0;
        double avgScore = 0;

        for (int k = 1; k <= 100000000; k++) {

            Board testBoard = new Board();
            int round = 0;
            int chooser = 0;
            do {
                round++;
//                System.out.printf("round: %d\n", round);
                testBoard.newBlock();
//                testBoard.printBoard();
                if (!testBoard.isAlive()) {
                    break;
                }
                Direction direction = null;
                while (direction == null) {
//                String command = in.nextLine();
//                switch (command.charAt(0)) {
//                    case 'a' -> direction = Direction.LEFT;
//                    case 'd' -> direction = Direction.RIGHT;
//                    case 'w' -> direction = Direction.UP;
//                    case 's' -> direction = Direction.DOWN;
//                }
                    int maxDeltaScore = 0;
                    chooser = (chooser + 1) % 4;
                    for (int i = 0; i < Direction.values().length; i++) {
                        if (testBoard.deltaScore(Direction.get(i)) > maxDeltaScore) {
                            maxDeltaScore = testBoard.deltaScore(Direction.get(i));
                            chooser = i;
                        }
                    }
                    direction = Direction.get(chooser);

                    if (testBoard.isLegal(direction)) {
                        testBoard.push(direction);
                    } else {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (Exception ignore) {
//
//                    }
//                        System.out.println("Illegal input!");
                        direction = null;
                    }
                }
//                System.out.println();
            } while (true);

            if (k == 1) {
                avgScore = testBoard.getScore();
                avgRound = round;
            } else {
                avgScore = avgScore / k * (k - 1) + (double) testBoard.getScore() / k;
                avgRound = avgRound / k * (k - 1)+ (double) round / k;
            }

            if (k % 10000 == 0) {
                System.out.printf("After %d turns\n", k);
                System.out.printf("Average Score: %.2f\n", avgScore);
                System.out.printf("Average Round: %.2f\n", avgRound);
                System.out.println();
            }
        }


    }
}




enum Direction {
    UP, DOWN, LEFT, RIGHT;
    public static Direction random() {
        Random ran = new Random();
        return get(ran.nextInt(4));
    }
    public static Direction get(int i) {
        Direction ret = null;
        if (i == 0) {
            ret = UP;
        } else if (i == 1) {
            ret = DOWN;
        } else if (i == 2) {
            ret = LEFT;
        } else if (i == 3) {
            ret = RIGHT;
        }
        assert ret != null;
        return ret;
    }
    public static int[][][] get(Direction direction) {
        int[][][] ret = new int[Board.getSize()][Board.getSize()][2];
        for (int i = 0; i < Board.getSize(); i++) {
            ret[i] = get(direction, i);
        }
        return ret;
    }
    public static int[][] get(Direction direction, int i) {
        int[][] ret = new int[Board.getSize()][2];
        if (direction == LEFT) {
            int count = 0;
            for (int j = Board.getSize() - 1; j >= 0; j--) {
                ret[count][0] = i;
                ret[count][1] = j;
                count++;
            }
        } else if (direction == RIGHT) {
            int count = 0;
            for (int j = 0; j < Board.getSize(); j++) {
                ret[count][0] = i;
                ret[count][1] = j;
                count++;
            }
        } else if (direction == UP) {
            int count = 0;
            for (int j = Board.getSize() - 1; j >= 0; j--) {
                ret[count][0] = j;
                ret[count][1] = i;
                count++;
            }
        } else if (direction == DOWN) {
            int count = 0;
            for (int j = 0; j < Board.getSize(); j++) {
                ret[count][0] = j;
                ret[count][1] = i;
                count++;
            }
        }
        return ret;
    }
    public static Direction getOppositeDirection(Direction direction) {
        Direction ret = null;
        if (direction == LEFT) {
            ret = RIGHT;
        } else if (direction == RIGHT) {
            ret = LEFT;
        } else if (direction == UP) {
            ret = DOWN;
        } else if (direction == DOWN) {
            ret = UP;
        }
        assert ret != null;
        return ret;
    }

    public static void main(String[] args) {

        for (Direction direction : Direction.values()) {
            System.out.printf("Direction: %s\n", direction);
            int[][][] pairs = Direction.get(direction);
            Board testBoard = new Board();
            int count = 0;
            for (int i = 0; i < Board.getSize(); i++) {
                for (int j = 0; j < Board.getSize(); j++) {
                    testBoard.setBlock(pairs[i][j], count);
                    count++;
                }
            }
            testBoard.printBoard();
            System.out.println();
        }
    }
}