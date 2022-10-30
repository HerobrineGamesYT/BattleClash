package net.herobrine.clashroyale.beta;

import java.util.Arrays;
import java.util.List;

public class Rainbow {

    private int place = 0;
    private String text = "You did not provide any text.";
    private String fancyText = "§You did not provide any text"; // gets reset anyway
    //Arrays.asList("§4", "§c", "§6", "§e", "§a", "§2", "§b", "§3", "§5", "§d");
    private final List<String> RAINBOW = Arrays.asList("§c", "§e", "§a", "§3", "§9"); // 10 strings

    public Rainbow(String text){
        place = 0;
        if(text != null){
            this.text = text;
        }
        updateFancy();
    }
    private void updateFancy(){
        int spot = place;
        String fancyText = "";
        for(char l : text.toCharArray()){
            String letter = Character.toString(l);
            if(!letter.equalsIgnoreCase(" ")){
                String t1 = fancyText;
                fancyText = t1 + RAINBOW.get(spot) + letter;
                if(spot == RAINBOW.size() - 1){
                    spot = 0;
                } else{
                    spot++;
                }
            } else {
                String t1 = fancyText;
                fancyText = t1 + letter;
            }
        }
        this.fancyText = fancyText;
    }

    public void moveRainbow(){
        if(RAINBOW.size() - 1 == place){
            place = 0;
        } else {
            place++;
        }
        updateFancy();
    }
    public String getOriginalText(){
        return text;
    }
    public String getText(){
        return fancyText;
    }
    public void setPlace(int place){
        if(place > RAINBOW.size() - 1){
            return;
        }
        this.place = place;
        updateFancy();
    }
    public int getPlace(){
        return place;
    }
    public List<String> getRainbow(){
        return RAINBOW;
    }

}
