package ride;

public class Rider {
    private String name;
    private Party party;

    public Rider(String name, Party party) {
        this.name = name;
        this.party = party;
    }

    public String getName() {
        return name;
    }

    public Party getParty() {
        return party;
    }
}
