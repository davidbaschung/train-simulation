package railway;

/**
 * Represents a switch with given position, of the miniature railway system.
 */
public class SwitchPosition extends RailwayElement {

    /**
     * the switch concerned
     */
    private Switch sw;

    /**
     * the position it should be setPosition to
     */
    private boolean position;

    /**
     * Constructor, takes as parameter a switch and the expected position.
     *
     * @param sw
     * @param position
     */
    public SwitchPosition(Switch sw, boolean position) {
        this.sw = sw;
        this.position = position;
    }

    /**
     * switches the switch to "position"
     */
    public void setPosition() {
        sw.setPosition(position);
    }

    public boolean getPosition() {
        return position;
    }

    /**
     * @return the sector which contains the switch
     */
    public Sector getSector() {
        return sw.getSector();
    }

    /**
     * @return the ID of the switch
     */
    @Override
    public String getId() {
        return sw.getId();
    }
}
