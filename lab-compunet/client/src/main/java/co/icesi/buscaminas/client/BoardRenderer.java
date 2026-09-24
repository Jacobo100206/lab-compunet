package co.icesi.buscaminas.client;

import co.icesi.buscaminas.client.model.Cell;

public class BoardRenderer {

    public static void printBoard(Cell[][] board) {
        System.out.println();
        System.out.print("   ");
        for (int j = 0; j < board[0].length; j++) {
            System.out.printf("%3d  ", j);
        }
        System.out.println();
        for (int i = 0; i < board.length; i++) {
            System.out.printf("%2d ", i);
            for (int j = 0; j < board[0].length; j++) {
                System.out.print("[ " + symbol(board[i][j]) + " ]");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static String symbol(Cell cell) {
        if (cell.isMarked()) {
            return "\u001B[33mM\u001B[0m";
        }
        if (cell.isHide() && !cell.isShowAll()) {
            return ".";
        }
        if (cell.isLandMine()) {
            return "\u001B[31m*\u001B[0m";
        }
        return cell.getValue() == 0 ? " " : cell.getValue() + "";
    }
}
