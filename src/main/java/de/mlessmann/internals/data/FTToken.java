package de.mlessmann.internals.data;

import de.mlessmann.api.data.IFTToken;

/**
 * Created by Life4YourGames on 16.11.16.
 */
public class FTToken implements IFTToken {

    private String token = null;
    private Direction direction = Direction.UNKNOWN;

    public FTToken(String token, Direction direction) {
        this.token = token;
        this.direction = direction;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }
}
