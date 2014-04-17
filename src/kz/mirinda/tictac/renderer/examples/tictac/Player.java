package kz.mirinda.tictac.renderer.examples.tictac;

/**
 *
 */
public interface Player {
    /**
     * уведомляет пользователя что у него начался новый ход<p>
     * notife user that he must started next movement
     */
    public void move();

    /**
     * Позволяет узнать сделал ли пользователь
     *  выбор следующего хода<p>
     * Lets find out if the user did            choice of next move
     *
     */
     public boolean isMoved();

    /**
     * Позволяет получить ход который был сделан пользователем<p>
     *
     * lets get user move
     */
    TicTacExample.Movement getMovement();
}
