package tlc.net;

import mhframework.io.net.MHMessageType;
import mhframework.io.net.MHNetworkMessage;

public abstract class TLCMessageType extends MHMessageType
{
    public static final String UPDATE_TEAM              = "Update team data";
    public static final String RECRUIT_CHARACTER        = "Recruit character";
    public static final String RETIRE_CHARACTER         = "Retire character";
    public static final String REGISTER_USER_TYPE       = "Register user type";
    public static final String UPDATE_CHARACTER         = "Update character data";
    public static final String UPDATE_PLAYER_DATA       = "Player descriptor update";
    public static final String SIGNAL_READY             = "Signal or cancel ready";
    public static final String BROADCAST_GAME_STATE     = "Broadcast game/lobby state";
    public static final String BROADCAST_MAP_FILE       = "Broadcast map file name";
    public static final String GAME_OPTIONS             = "Set game options";
    public static final String PUT_OBJECT               = "Put object on game board";
    public static final String BROADCAST_CHARACTER_LIST = "Broadcast character list";
    public static final String REQUEST_MOVE_POINTS      = "Request movement points";
    public static final String CHARACTER_MOVE           = "Character walk";
    public static final String WHOSE_TURN               = "Broadcast whose turn";
    public static final String DRAW_TOKEN               = "Draw token";
    public static final String EVENT_LOG                = "Event log message.";
    public static final String ATTACK                   = "Attack Action";
    public static final String DEFEND                   = "Defend Action";
    public static final String COMBAT_RESULTS           = "Broadcast Combat Results";
    public static final String ATTACK_NOTIFICATION      = "Notify Defender of Attack";
}
