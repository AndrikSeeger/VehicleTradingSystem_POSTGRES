import Dateien.Database;
import Dateien.PrintableObject;
import Listen.Filter;
import Listen.ListeAktionen;
import Listen.Printer;
import Listen.Sorter;
import Exceptions.*;
import Sellers.*;
import Sellers.Exceptions.NoUserLoggedIn;
import Vehicles.*;
import SQL.*;

import java.io.File;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.Scanner;

import Menu.*;

import javax.xml.crypto.Data;

import static Dateien.Csv.delimiter;

/**
 * Klasse für GUI Funktionalität
 *
 * @author 1319658
 * @version 10.3
 */
public class UserMenu {

    /* Menü nicht für Funktionalität relevant deshalb nicht ausführlich kommentiert */

    public static void main(String[] args) {
        userMenu();
    }

    public static void userMenu() {
        String tableInserate = "inserate";
        String tableAutos = "model_auto";
        String tableLKWs = "model_lkw";
        String tableZusatzLKW = "za_lkw";
        String tableZusatzAuto = "za_auto";
        String tablePrivatAcc = "privat_acc";
        String tableProAcc = "pro_acc";


        int maxFahrzeuge = 10; //Maximale Anzahl an Fahrzeugen pro Nutzer


        int wahl = -1;
        Long keyAcc = 0L; //Key des aktuell angemeldeten Accounts
        IdNummer idSupp;
        Database database;

        Scanner scanner = new Scanner(System.in);
        System.out.print("Database URL eingeben: ");
        String url = scanner.nextLine();
        System.out.print("Database Username eingeben: ");
        String username = scanner.nextLine();
        System.out.print("Database Passwort eingeben: ");
        String passwort = scanner.nextLine();
        MenuText.printBlanc();

        try {
            //database = new Database("jdbc:postgresql://localhost:5432/test", "postgres", "1234");
            database = new Database(url, username, passwort);
        } catch (NoSuchDB e) {
            throw new RuntimeException("Verbindung nicht möglich.");
        }

        database.createTables(SQLCode.getReqTables(), SQLCode.getCreateCode(), "Neue Tables wurden erstellt!");

        ArrayList<FahrzeugBasis> fahrzeuge = database.loadVehicles(tableInserate, tableAutos, tableLKWs, tableZusatzLKW, tableZusatzAuto);
        ArrayList<AccountBasis> accounts = database.loadAccounts(tablePrivatAcc, tableProAcc);

        idSupp = initalizeId(database, tablePrivatAcc, tableProAcc, accounts);

        MenuText.message("Willkommen beim Fahrzeugmarkt!");

        MenuText.message("Hinweis: Das Zeichen '" + delimiter + "' darf bei keiner Eingabe verwendet werden");

        while(true) {
            try {
                MenuText.printBlanc();
                MenuText.menuHaupt();

                switch (getNumber(0, 7))
                {
                    case 0:
                        boolean goBack = false;
                        ArrayList<FahrzeugBasis> resultList = fahrzeuge;


                        while(true)
                        {
                            Printer.printList(combineLists(resultList, accounts));
                            //ListeAktionen.printList(resultList); //,accounts)
                            MenuText.printBlanc();
                            MenuText.printBlanc();
                            MenuText.message("- 0 = Zurück --- 1 = Sortieren --- 2 = Filtern -");
                            switch (getNumber(0, 2))
                            {
                                case 0:
                                    throw new AbortActionException();
                                    //goBack = true;
                                    //break;
                                case 1:
                                    MenuText.menuSortieren();
                                    resultList = Sorter.sortList(resultList, FahrzeugVergleicher.returnCompInfo(getNumber(0, 7)));
                                    break;
                                case 2: //LKW // Auto
                                    MenuText.message("- 0 = Zurück --- 1 = Auto --- 2 = Lkw -");
                                    switch (getNumber(0, 2))
                                    {
                                        case 0:
                                            MenuText.printBlanc();
                                            MenuText.message("zurück...");
                                            break;
                                        case 1:
                                            MenuText.printBlanc();
                                            MenuText.menuFilternBasis();
                                            MenuText.menuFilterAuto();
                                            MenuText.printBlanc();
                                            resultList = switch (getNumber(0, 8)) {
                                                case 0 -> Filter.filterList(FahrzeugFilter.getFilter("getMarke", Auto.class), resultList);
                                                case 1 -> Filter.filterList(FahrzeugFilter.getFilter("getModell", Auto.class), resultList);
                                                case 2 -> Filter.filterList(FahrzeugFilter.getFilter("getPreis", Auto.class), resultList);
                                                case 3 -> Filter.filterList(FahrzeugFilter.getFilter("getLeistung", Auto.class), resultList);
                                                case 4 -> Filter.filterList(FahrzeugFilter.getFilter("getOdometer", Auto.class), resultList);
                                                case 5 -> Filter.filterList(FahrzeugFilter.getFilter("getGewicht", Auto.class), resultList);
                                                case 6 -> Filter.filterList(FahrzeugFilter.getFilter("getErstzulassung", Auto.class), resultList);
                                                case 7 -> Filter.filterList(FahrzeugFilter.getFilter("getFahrzeugArt", Auto.class), resultList);
                                                case 8 -> Filter.filterList(FahrzeugFilter.getFilter("getSportPaket", Auto.class), resultList);
                                                default -> resultList;
                                            };
                                            break;
                                        case 2:
                                            MenuText.printBlanc();
                                            MenuText.menuFilternBasis();

                                            MenuText.message("7 = Zuladung filtern: ");
                                            MenuText.message("8 = Zuglast filtern: ");
                                            MenuText.message("9 = Hydraulik (ja/nein): ");

                                            MenuText.printBlanc();
                                            resultList = switch (getNumber(0, 9)) {
                                                case 0 -> Filter.filterList(FahrzeugFilter.getFilter("getMarke", Lkw.class), resultList);
                                                case 1 -> Filter.filterList(FahrzeugFilter.getFilter("getModell", Lkw.class), resultList);
                                                case 2 -> Filter.filterList(FahrzeugFilter.getFilter("getPreis", Lkw.class), resultList);
                                                case 3 -> Filter.filterList(FahrzeugFilter.getFilter("getLeistung", Lkw.class), resultList);
                                                case 4 -> Filter.filterList(FahrzeugFilter.getFilter("getOdometer", Lkw.class), resultList);
                                                case 5 -> Filter.filterList(FahrzeugFilter.getFilter("getGewicht", Lkw.class), resultList);
                                                case 6 -> Filter.filterList(FahrzeugFilter.getFilter("getErstzulassungString", Lkw.class), resultList);
                                                case 7 -> Filter.filterList(FahrzeugFilter.getFilter("getZuladung", Lkw.class), resultList);
                                                case 8 -> Filter.filterList(FahrzeugFilter.getFilter("getZuglast", Lkw.class), resultList);
                                                case 9 -> Filter.filterList(FahrzeugFilter.getFilter("getHydraulik", Lkw.class), resultList);
                                                default -> resultList;
                                            };
                                            break;
                                    }

                                    //menuAuto();


                                    //menuFiltern();
                                    break;
                            }
                        }
                    case 1:
                        AccountAktionen.angemeldet(keyAcc);
                        if(FahrzeugSuche.fahrzeugeZuKey(fahrzeuge, keyAcc).size() < maxFahrzeuge)
                        {
                            MenuText.menuInserieren();
                            switch (getNumber(0, 2))
                            {
                                case 0:
                                    MenuText.printBlanc();
                                    MenuText.message("zurück...");
                                    break;
                                case 1:
                                    ListeAktionen.addVec(database, fahrzeuge, Menu.createNewObject("Vehicles.Auto", keyAcc, delimiter), tableInserate,
                                            tableAutos, tableLKWs, tableZusatzLKW, tableZusatzAuto, tablePrivatAcc, tableProAcc);
                                    break;
                                case 2:
                                    ListeAktionen.addVec(database, fahrzeuge, Menu.createNewObject("Vehicles.Lkw", keyAcc, delimiter), tableInserate,
                                            tableAutos, tableLKWs, tableZusatzLKW, tableZusatzAuto, tablePrivatAcc, tableProAcc);
                                    break;
                            }
                        }
                        else
                        {
                            MenuText.message("Maximale Anzahl von " + maxFahrzeuge + " Inseraten erreicht!");
                        }
                        break;
                    case 2:
                        AccountAktionen.angemeldet(keyAcc);
                        resultList = FahrzeugSuche.fahrzeugeZuKey(fahrzeuge, keyAcc);
                        if(resultList.size() == 0)
                        {
                            MenuText.message("Keine Fahrzeuge inseriert!");
                            break;
                        }
                        Printer.printList(resultList);
                        break;
                    case 3:
                            AccountAktionen.angemeldet(keyAcc);
                            resultList = FahrzeugSuche.fahrzeugeZuKey(fahrzeuge, keyAcc);
                            Printer.printListWithNumbers(resultList);
                            if(resultList.size() == 0)
                            {
                                MenuText.message("Keine Fahrzeuge inseriert!");
                                break;
                            }
                            wahl = getNumber(0, resultList.size()-1);
                            MenuText.message("- 0 = Zurück --- 1 = Löschen --- 2 = Ersetzen -");
                            switch (getNumber(0, 2))
                            {
                                case 0:
                                    MenuText.printBlanc();
                                    MenuText.message("zurück...");
                                    break;
                                case 1:
                                    ListeAktionen.deleteObject(database, fahrzeuge, resultList.get(wahl), tableInserate, keyAcc);
                                    MenuText.message("Inserat wurde gelöscht");
                                    break;
                                case 2:
                                    String classVehicle = resultList.get(wahl).getClass().getSimpleName();
                                    FahrzeugBasis newVehicle;
                                    switch(classVehicle)
                                    {
                                        case "Auto": newVehicle = Menu.createNewObject("Vehicles.Auto", keyAcc, delimiter); break;
                                        case "Lkw":  newVehicle = Menu.createNewObject("Vehicles.Lkw", keyAcc, delimiter); break;
                                        default:
                                            throw new IllegalStateException("Unexpected value: " + classVehicle);
                                    }

                                    ListeAktionen.replaceObject(database, fahrzeuge, resultList.get(wahl), newVehicle, tableInserate, tableAutos, tableLKWs, tableZusatzLKW, tableZusatzAuto,tablePrivatAcc,tableProAcc,keyAcc);
                            }

                        break;

                    case 4:
                        if(keyAcc == 0L)
                        {
                            keyAcc = AccountAktionen.login(accounts);
                        }
                        else
                        {
                            MenuText.message("User bereits angemeldet!");
                        }
                        break;
                    case 5:
                        keyAcc = AccountAktionen.logout(keyAcc);
                        break;
                    case 6:
                        MenuText.menuAccountErstellen();
                        switch (getNumber(0, 2))
                        {
                            case 0:
                                throw new AbortActionException();
                            case 1:
                                AccountBasis erstellterAccount = (Account) Menu.createNewObject("Sellers.Account", idSupp, delimiter);
                                addAccount(database, accounts, erstellterAccount, tablePrivatAcc, tableProAcc);
                                break;
                            case 2:
                                AccountBasis erstellterAccountPro = (AccountPro) Menu.createNewObject("Sellers.AccountPro", idSupp, delimiter);
                                addAccount(database, accounts, erstellterAccountPro, tablePrivatAcc, tableProAcc);
                                break;
                        }
                        break;
                    case 7:
                            AccountAktionen.angemeldet(keyAcc);
                            ListeAktionen.deleteObjects(database, AccountAktionen.getAcc(accounts, keyAcc), accounts, fahrzeuge, FahrzeugSuche.fahrzeugeZuKey(fahrzeuge, keyAcc), tablePrivatAcc, tableProAcc);
                            keyAcc = 0L;
                            MenuText.message("Account wurde gelöscht");
                        break;
                }
            }
            catch (AbortActionException e) //Nutzer will Eingabe abbrechen
            {
                MenuText.printBlanc();
                MenuText.message("zurück...");
            }
            catch (NoUserLoggedIn e)
            {
                MenuText.printBlanc();
                MenuText.message("Kein Nutzer angemeldet!");
            } catch (NoSuchObjectException e) {
                e.printStackTrace();
                throw new RuntimeException("Fehler in Liste");
            }
        }
    }

    private static <T1 extends FahrzeugBasis & PrintableObject, T2 extends AccountBasis & PrintableObject, T3 extends PrintableObject> ArrayList<T3> combineLists (ArrayList<T1> fahrzeuge, ArrayList<T2> accounts)
    {
        ArrayList<T3> combinedList = new ArrayList<>();
        for (T1 fzg : fahrzeuge) {
            combinedList.add((T3) fzg);
            combinedList.add((T3) AccountAktionen.getAcc(accounts, fzg.getKey()));
        }

        return combinedList;
    }


    private static IdNummer initalizeId(Database db, String tablePrivat, String tablePro, ArrayList<AccountBasis> accounts) {
        IdNummer idSupp;
        if(accounts.size() == 0) //Liste neu erstellt --> Keine Id-Supplier in Liste
        {
            idSupp = new IdNummer(0L);
        }
        else
        {
            idSupp = new IdNummer((long) db.getCurrentID(tablePrivat, tablePro));
        }
        return idSupp;
    }

    private static void addAccount(Database db, ArrayList<AccountBasis> accounts, AccountBasis erstellterAccount, String tablePriv, String tablePro) {
        if(AccountAktionen.checkExist(accounts, erstellterAccount) == 0L)
        {
            ListeAktionen.addAcc(db, accounts, erstellterAccount, tablePriv, tablePro);
            MenuText.printBlanc();
            MenuText.message("Account erstellt!");

        }
        else
        {
            MenuText.message("Fehler. E-Mail-Adresse bereits vorhanden.");
        }
    }

    //Abfrage Nummer von Konsole. Min mindestens 0 und Max maximal 9. Gibt entweder Nummer oder -1 zurück
    private static int getNumber(int min , int max)
    {
        Scanner scanner = new Scanner(System.in);
        String regex = "[" + min + "-" + max + "]";
        String input;
        int wahl = -1;

        while(wahl == -1)
        {
            System.out.print("Nummer eingeben: ");
            input = scanner.nextLine();
            if(input.matches(regex))
            {
                wahl = Integer.parseInt(input);
            }
            else
            {
                MenuText.message("Falsche Eingabe!");
            }
        }
        MenuText.printBlanc();
        return wahl;
    }

}
