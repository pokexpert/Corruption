package Modele;

import Modele.Cartes.Abstacts.Building;
import Modele.Cartes.Abstacts.Card;
import Modele.Cartes.Abstacts.Event;
import Modele.Cartes.Abstacts.Character;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by bapti on 08/03/2017.
 */
public class Partie {
    private int influence;
    private int influenceMod = 0;


    private int appreciationMalus = 0;
    private int credits;
    private int creditsMod = 2;
    private int appreciation;
    private int tours;
    private boolean batimentsEnabled;

    private ListeCarte activeCards;
    private ListeCarte paquet;
    private ListeCarte cardsToAdd;

    public Partie(int tours) {
        activeCards = new ListeCarte();
        this.influence = 3;
        this.appreciation = 20;
        this.credits = 20;
        this.tours = tours;
        this.cardsToAdd = new ListeCarte();
        paquet = PaquetFactory.testDeck(this);
        paquet.melanger();
    }


    private void run() {
        try {
            System.out.println("Bienvenue... vous avez "+tours+" tours a tenir... bonne chance...");
            for (int ktours = 0; ktours < tours; ktours++) {
                appreciationMalus = 0;
                creditsMod = 0;
                influenceMod = 0;
                batimentsEnabled=true;
                for (Card c : activeCards.getListe()) {
                    c.onTime();
                }
                int apr = appreciation - appreciationMalus;
                int infl = influence + influenceMod;

                checkDefaite();

                    StringBuilder sb = new StringBuilder();
                    sb.append("\n-------Nouveau Tour--------\n");
                    sb.append("Vous avez ").append(apr).append(" points d'appreciation (").append(appreciation).append(" - ").append(appreciationMalus).append(")\n");
                    sb.append("Vous avez ").append(infl).append(" points d'influence (").append(influence).append(" - ").append(influenceMod).append(")\n");
                    sb.append("Vous avez ").append(credits).append(" credits (").append(creditsMod).append(" par tour)\n");
                if (activeCards.getListe().size() != 0) {
                    sb.append("Vous avez sur le terrain :\n");
                    for (Card c : activeCards.getListe()) {
                        sb.append(c.describe(true)).append("\n");
                    }
                    sb.append("-----------\n");


                }
                System.out.print(sb.toString());
                ListeCarte choisies = new ListeCarte();
                ListeCarte piochees = new ListeCarte();
                piochees.getListe().add(paquet.draw());
                for (int i = 1; i < influence + influenceMod; i++)
                    piochees.getListe().add(paquet.draw());

                for (Card c : piochees.clone().getListe()) {
                    if (c.isInsurmontable()) {
                        choisies.getListe().add(piochees.removeCarte(c));
                    }
                }
                if (choisies.getListe().size() == 0) {


                    int choix = 0;
                    while (choix > piochees.getListe().size() || choix < 1) {
                        try {
                            System.out.println("Choisisez une carte :");
                            for (int i = 0; i < piochees.getListe().size(); i++)
                                System.out.println((i + 1) + " : " + piochees.getListe().get(i).describe(true));
                            Scanner s = new Scanner(System.in);
                            choix = s.nextInt();
                        } catch (Exception ignored) {

                        }
                    }
                    choisies.getListe().add(piochees.getListe().remove(choix - 1));
                } else {
                    System.out.println("Insurmonatble !!");
                    for (Card c : choisies.getListe())
                        System.out.println(c.describe(true));
                }
                paquet.getListe().addAll(piochees.getListe());
                paquet.melanger();
                for (Card c : choisies.getListe()) {
                    c.onArrive();
                    cardsToAdd.getListe().add(c);
                }
                for (Card c : new ArrayList<>(activeCards.getListe())) {
                    c.applyTour();
                }
                if (batimentsEnabled) {

                    ListeCarte batiments = new ListeCarte();
                    for (Card c : activeCards.getListe()) {
                        if (c instanceof Building)
                            batiments.getListe().add(c);
                    }
                    if (batiments.getListe().size() > 0) {
                        int choix = -1;
                        while (choix < 0 || choix > batiments.getListe().size()) {
                            System.out.println("Editez vos batiments : choisissez un batiment a desactiver ou entrez 0 pour acun");
                            for (int i = 0; i < batiments.getListe().size(); i++) {
                                System.out.println((i + 1) + " : " + batiments.getListe().get(i).describe(true));
                            }
                            Scanner s = new Scanner(System.in);
                            choix = s.nextInt();
                        }
                        if (choix != 0) {
                            editPermanentCredits(-2);
                            batiments.getListe().get(choix - 1).disable();
                        }


                    }
                } else
                    System.out.println("Batiments non modifiables !");
                credits += creditsMod;
                for (Card c : cardsToAdd.getListe()) {
                    if (!(c instanceof Event))
                        activeCards.getListe().add(c);
                }
                cardsToAdd = new ListeCarte();
            }
            System.out.println("Vous avez survecu");
        } catch (Defaite d) {
            System.out.println("Vous avez perdu!");
        }
    }
    private void decrireTerrain(){

    }
    private void checkDefaite() throws Defaite {
        if (appreciation - appreciationMalus <= 0) {
            System.out.println("Le peuple se rebelle ! : "+getTotalAppreciation());
            for (Card c : activeCards.getListe()) {
                System.out.println(c.describe(true));
            }
            throw new Defaite();
        }
    }

    public void editCreditsMod(int creditsMod) {
        this.creditsMod += creditsMod;
    }


    public void editInfluenceMod(int influenceMod) {
        this.influenceMod += influenceMod;
    }

    public void editAppreciationMod(int appreciationMod) {
        this.appreciationMalus += appreciationMod;
    }

    public void disableBatiments() {
        this.batimentsEnabled = false;
    }

    public void editPermanentInfluence(int influence) {
        this.influence += influence;
    }

    public void editPermanentCredits(int credits) {
        this.credits += credits;
    }

    public void editPermanentAppreciation(int appreciation) {
        this.appreciation += appreciation;
    }

    public ListeCarte getActiveCards() {
        return activeCards;
    }

    public ListeCarte getPaquet() {
        return paquet;
    }

    public ListeCarte getCardsToAdd() {
        return cardsToAdd;
    }

    public int getTotalCredits() {
        return credits;
    }

    public int getTotalInfluence() {
        return influence + influenceMod;
    }

    public int getTotalAppreciation() {
        return appreciation - appreciationMalus;
    }

    public static void main(String[] args) {
        new Partie(8).run();
    }

}

