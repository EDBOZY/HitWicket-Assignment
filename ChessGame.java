import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

abstract class Character {
    String name;
    char player;
    int x, y;

    public Character(String name, char player, int x, int y) {
        this.name = name;
        this.player = player;
        this.x = x;
        this.y = y;
    }

    abstract boolean move(String direction, Character[][] grid);

    boolean inBounds(int x, int y) {
        return x >= 0 && x < 5 && y >= 0 && y < 5;
    }

    void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    int[] getPosition() {
        return new int[]{x, y};
    }
}

class Pawn extends Character {
    public Pawn(String name, char player, int x, int y) {
        super(name, player, x, y);
    }

    @Override
    boolean move(String direction, Character[][] grid) {
        int dx = 0, dy = 0;
        switch (direction) {
            case "L": dy = -1; break;
            case "R": dy = 1; break;
            case "F": dx = -1; break;
            case "B": dx = 1; break;
            default: return false;
        }
        int newX = x + dx, newY = y + dy;
        if (inBounds(newX, newY) && updatePosition(newX, newY, grid)) {
            return true;
        }
        return false;
    }

    boolean updatePosition(int newX, int newY, Character[][] grid) {
        if (grid[newX][newY] == null || grid[newX][newY].player != player) {
            grid[x][y] = null;
            setPosition(newX, newY);
            grid[newX][newY] = this;
            return true;
        }
        return false;
    }
}

class Hero1 extends Character {
    public Hero1(String name, char player, int x, int y) {
        super(name, player, x, y);
    }

    @Override
    boolean move(String direction, Character[][] grid) {
        int dx = 0, dy = 0;
        switch (direction) {
            case "L": dy = -2; break;
            case "R": dy = 2; break;
            case "F": dx = -2; break;
            case "B": dx = 2; break;
            default: return false;
        }
        int newX = x + dx, newY = y + dy;
        if (inBounds(newX, newY) && updatePosition(newX, newY, grid)) {
            return true;
        }
        return false;
    }

    boolean updatePosition(int newX, int newY, Character[][] grid) {
        int midX = (x + newX) / 2;
        int midY = (y + newY) / 2;
        if (grid[midX][midY] != null && grid[midX][midY].player != player) {
            grid[midX][midY] = null; // Remove opponent's character in the path
        }
        if (grid[newX][newY] == null || grid[newX][newY].player != player) {
            grid[x][y] = null;
            setPosition(newX, newY);
            grid[newX][newY] = this;
            return true;
        }
        return false;
    }
}

class Hero2 extends Character {
    public Hero2(String name, char player, int x, int y) {
        super(name, player, x, y);
    }

    @Override
    boolean move(String direction, Character[][] grid) {
        int dx = 0, dy = 0;
        switch (direction) {
            case "FL": dx = -2; dy = -2; break;
            case "FR": dx = -2; dy = 2; break;
            case "BL": dx = 2; dy = -2; break;
            case "BR": dx = 2; dy = 2; break;
            default: return false;
        }
        int newX = x + dx, newY = y + dy;
        if (inBounds(newX, newY) && updatePosition(newX, newY, grid)) {
            return true;
        }
        return false;
    }

    boolean updatePosition(int newX, int newY, Character[][] grid) {
        int midX = (x + newX) / 2;
        int midY = (y + newY) / 2;
        if (grid[midX][midY] != null && grid[midX][midY].player != player) {
            grid[midX][midY] = null; // Remove opponent's character in the path
        }
        if (grid[newX][newY] == null || grid[newX][newY].player != player) {
            grid[x][y] = null;
            setPosition(newX, newY);
            grid[newX][newY] = this;
            return true;
        }
        return false;
    }
}

class Game {
    Character[][] grid;
    Map<Character, Character> players;
    char currentPlayer;

    public Game() {
        grid = new Character[5][5];
        players = new HashMap<>();
        currentPlayer = 'A';
    }

    void setup() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Player A setup:");
        setupPlayer('A', scanner);
        System.out.println("Player B setup:");
        setupPlayer('B', scanner);
    }

    void setupPlayer(char player, Scanner scanner) {
        System.out.printf("Enter the positions of %c's characters (e.g., P1 H1 H2 P2 P3): ", player);
        String[] positions = scanner.nextLine().split(" ");
        int row = player == 'A' ? 0 : 4;
        for (int i = 0; i < positions.length; i++) {
            String charName = positions[i];
            Character character;
            if (charName.startsWith("P")) {
                character = new Pawn(charName, player, row, i);
            } else if (charName.equals("H1")) {
                character = new Hero1(charName, player, row, i);
            } else if (charName.equals("H2")) {
                character = new Hero2(charName, player, row, i);
            } else {
                continue;
            }
            players.put(charName.charAt(0), character);
            grid[row][i] = character;
        }
    }

    void displayGrid() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == null) {
                    System.out.print(". ");
                } else {
                    System.out.print(grid[i][j].player + "-" + grid[i][j].name + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    void play() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            displayGrid();
            System.out.printf("Player %c, enter your move: ", currentPlayer);
            String move = scanner.nextLine();
            if (!processMove(move)) {
                System.out.println("Invalid move, try again.");
                continue;
            }
            if (checkWin()) {
                displayGrid();
                System.out.printf("Player %c wins!%n", currentPlayer);
                break;
            }
            currentPlayer = (currentPlayer == 'A') ? 'B' : 'A';
        }
    }

    boolean processMove(String move) {
        try {
            String[] parts = move.split(":");
            String charName = parts[0];
            String direction = parts[1];
            Character character = players.get(charName.charAt(0));
            if (character != null && character.player == currentPlayer) {
                return character.move(direction, grid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    boolean checkWin() {
        char opponent = (currentPlayer == 'A') ? 'B' : 'A';
        for (Character[] row : grid) {
            for (Character cell : row) {
                if (cell != null && cell.player == opponent) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.setup();
        game.play();
    }
}
