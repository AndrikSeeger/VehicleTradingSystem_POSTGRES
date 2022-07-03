package Listen;

import Dateien.Csv;
import Dateien.Database;
import Sellers.Account;
import Sellers.AccountBasis;
import Vehicles.FahrzeugBasis;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Klasse um Listen und Datenbank konsistent zu bearbeiten
 *
 * @author 1319658
 * @version 3.3
 */

public class ListeAktionen {

    //Instanz eines Fahrzeugs durch eine andere ersetzen (Inserat ersetzen)
    public static void replaceObject(Database db, ArrayList<FahrzeugBasis> liste, FahrzeugBasis oldObject, FahrzeugBasis newObject, String tableInserate, String tableAuto, String tableLkws, String tableZusatzLkw,
                                     String tableZusatzAuto, String tableAccPriv, String tableAccPro, long accKey) throws NoSuchObjectException {
        if (liste.contains(oldObject)) //Prüft ob zu entfernendes Objekt in Liste enthalten ist
        { //Objekt enthalten
            liste.set(liste.indexOf(oldObject), newObject); //Altes Object durch Neues ersetzen
            db.deleteInserat(oldObject, tableInserate, accKey); //Altes Objekt aus DB löschen
            db.safeVehicle(newObject, tableInserate, tableAuto, tableLkws, tableZusatzLkw, tableZusatzAuto, tableAccPriv, tableAccPro); //Neues Object in DB sichern
        } else { //Objekt nicht enthalten
            throw new NoSuchObjectException("Zu ersetzendes Objekt nicht in Liste enthalten");
        }
    }

    //Fahrzeug/Inserat löschen
    public static void deleteObject(Database db, ArrayList<FahrzeugBasis> liste, FahrzeugBasis objectToDelete, String tableInserate, long accKey) throws NoSuchObjectException {
        if (liste.contains(objectToDelete)) //Prüft ob zu entfernendes Objekt in Liste enthalten ist
        { //Objekt enthalten
            liste.remove(objectToDelete); //Objekt aus Liste entfernen
            db.deleteInserat(objectToDelete, tableInserate, accKey); //Objekt aus DB löschen
        } else { //Objekt nicht enthalten
            throw new NoSuchObjectException("Zu löschendes Objekt nicht in Liste enthalten");
        }
    }

    //Account und die damit verknüpften Fahrzeuge löschen
    public static void deleteObjects(Database db, AccountBasis acc, ArrayList<AccountBasis> listeAcc, ArrayList<FahrzeugBasis> listeVec, ArrayList<FahrzeugBasis> elementsToDelete, String tablePriv, String tablePro) {
        listeVec.removeAll(elementsToDelete); //Fahrzeuge löschen
        listeAcc.remove(acc); //Account löschen
        db.deleteAcc(acc, tablePriv, tablePro); //Account und Fahrzeuge aus DB löschen
    }

    //Neuer Account hinzufügen
    public static <T extends AccountBasis> void addAcc(Database db, ArrayList<T> liste, T newObject, String tablePrivat, String tablePro) //Account-Object hinzufügen zur Liste und speichern
    {
        liste.add(newObject); //Neues Objekt zur Liste hinzufügen
        db.safeAccount(tablePrivat, tablePro, newObject); //Neues Objekt zu DB hinzufügen
    }

    //Neues Fahrzeug hinzufügen
    public static <T extends FahrzeugBasis> void addVec(Database db, ArrayList<T> liste, T newObject, String tableInserate, String tableAuto, String tableLkws,
                                                        String tableZusatzLkw, String tableZusatzAuto, String tableAccPriv, String tableAccPro) //Account-Object hinzufügen zur Liste und speichern
    {
        liste.add(newObject); //Neues Objekt zur Liste hinzufügen
        db.safeVehicle(newObject, tableInserate, tableAuto, tableLkws, tableZusatzLkw, tableZusatzAuto, tableAccPriv, tableAccPro);  //Neues Objekt in DB sichern
    }
}
