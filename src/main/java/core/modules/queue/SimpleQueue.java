package core.modules.queue;

import com.google.gson.annotations.Expose;
import core.modules.queue.exceptions.PersonNotFoundException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * @author Arthur Kupriyanov
 */
public class SimpleQueue extends Queue
                            implements  Swapable,
                                        QueueReturnable<TreeMap<Integer, Person>>,
                                        StatReturnable,
                                        FormattedQueueReturnable{

    {
        type = "simple";
    }

    // lists for access rights
    protected ArrayList<String> readAccessList = new ArrayList<>();
    protected ArrayList<String> writeAccessList = new ArrayList<>();
    protected ArrayList<String> executeAccessList = new ArrayList<>();

    /** Contains users VKID in queue */
    protected HashSet<Integer> users = new HashSet<>();

    /**seat on queue, person class*/
    @Expose
    protected TreeMap<Integer, Person> queue = new TreeMap<>();

    /** For statistic */
    @Expose
    protected Stat stat = new Stat();

    /** Current place in queue*/
    @Expose
    private int currentPlace = 0;

    /** Field for saving free ID*/
    @Expose
    private int freeId = 0;

    /** Queue name */
    @Expose
    private String name;

    /** Queue description*/
    @Expose
    private String description;

    /** for requests*/
    public Request request = new Request();

    public SimpleQueue(String name){
        this.name = name;
    }

    @Override
    public void saveQueue() throws IOException {
        final String FOLDER_NAME = "queue";
        Path pathToCatalog = Paths.get(FOLDER_NAME);
        if (Files.notExists(pathToCatalog)){
            new File("queue").mkdir();
        }
        String path = FOLDER_NAME + "/";
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path + this.name));
        oos.writeObject(this);
        oos.close();
    }

    @Override
    public String getQueueName() {
        return name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStat(Stat stat){
        this.stat = stat;
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public void addPerson(Person... persons) {
        for (Person person: persons
             ) {
            if (!users.contains(person.getVKID())) {
                if (person.getId() < freeId) {
                    person.setId(freeId);
                }
                if (queue.isEmpty()) {
                    queue.put(freeId, person);
                } else {
                    queue.put(queue.lastKey() + 1, person);
                }
                this.freeId++;
                this.stat.peopleCount++;
                addUserID(person.getVKID());
            }
        }
    }

    @Override
    public void deletePerson(int ... ids) {
        ArrayList<Integer> removableKeys = new ArrayList<>();

        for (int id: ids
             ) {

            for (int key: queue.keySet()
            ) {
                if (queue.get(key).getId() == id){
                    removableKeys.add(key);

                    // Если удаляемый персонаж текущий в очереди, текущим выбираем следующего
                    if (id == currentPlace){
                        this.incrementCurrentPlace();
                    }
                }
            }
        }
        for (int key: removableKeys
             ) {
            deleteUser(queue.get(key).getVKID());
            queue.remove(key);
        }

        if (queue.isEmpty()){
            freeId = 0;
            currentPlace = 0;
        }
    }

    @Override
    public int getCurrentPersonID() throws PersonNotFoundException {
        if (queue.isEmpty()){
            throw new PersonNotFoundException("Персонаж с указанным ID не найден");
        }
        try {
            return queue.get(currentPlace).getId();
        } catch (NullPointerException e){
            currentPlace = queue.get(queue.firstKey()).getId();
            return queue.get(currentPlace).getId();
        }
    }

    @Override
    public int getNextPersonID(int step) throws PersonNotFoundException {

        if (queue.containsKey(currentPlace + step)){
            return queue.get(currentPlace + step).getId();
        } else{
            throw new PersonNotFoundException("Не найден персонаж с таким ID");
        }
    }

    @Override
    public Person getCurrentPerson() throws PersonNotFoundException {
        if (!queue.isEmpty()){
            return this.getPerson(currentPlace);
        } else{
            throw new PersonNotFoundException("Очередь пустая");
        }
    }

    @Override
    public Person getPerson(int id) throws PersonNotFoundException {
        for (Person p: queue.values()
             ) {
            if (p.getId() == id){
                return p;
            }
        }

        throw new PersonNotFoundException("Персонаж не найден");
    }

    @Override
    public void personPassed(int id) {
        ArrayList<Integer> removeableKeys = new ArrayList<>();

        if (!queue.isEmpty()) {
            for (int key : queue.keySet()
            ) {
                if (queue.get(key).getId() == id) {
                    removeableKeys.add(key);
                    this.incrementCurrentPlace();
                    this.stat.passCount++;
                }
            }
        }

        for (int key: removeableKeys
             ) {
            queue.remove(key);
        }
    }

    @Override
    public boolean checkExist(int id) {
        for (Person person: queue.values()
             ) {
            if (person.getId() == id){
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean checkExist(int ... ids) {
        for (int id: ids
             ) {
            if (!checkExist(id)){
                return false;
            }
        }

        return true;
    }

    /**
     * Swaps the queue by ID
     * @param firstId first person ID
     * @param secondId second person ID
     * @throws PersonNotFoundException throws if person not found by <code>id</code>
     */
    @Override
    public void swap(int firstId, int secondId) throws PersonNotFoundException {
        Person firstPerson = null;
        Integer firstPersonKey = null;

        Person secondPerson = null;
        Integer secondPersonKey = null;

        for (Integer key: queue.keySet()
             ) {
            Person person = queue.get(key);
            int personId = person.getId();
            if (personId == firstId){
                firstPerson = person;
                firstPersonKey = key;
            }
            if (personId == secondId) {
                secondPerson = person;
                secondPersonKey = key;
            }
        }

        if (firstPerson != null && secondPerson != null) {
            queue.replace(firstPersonKey, secondPerson);
            queue.replace(secondPersonKey, firstPerson);
        } else{
            throw new PersonNotFoundException("Искомый персонаж не найден по ID");
        }
    }

    public int getFreeId(){
        return this.freeId;
    }

    public void setFreeId(int id) {
        if (id > this.freeId){
            this.freeId = id;
        }
    }
    private void incrementCurrentPlace(){
        for (int key: queue.keySet()
             ) {
            if (key > this.currentPlace){
                this.currentPlace = key;
                break;
            }
        }
    }

    @Override
    public TreeMap<Integer, Person> getQueue() {
        return queue;
    }

    @Override
    public Stat getStat() {
        return stat;
    }

    public String getFormattedQueue(){
        StringBuilder response = new StringBuilder();

        response.append("------------\n").append(getQueueName()).append("\n------------\n");
        for (Person person: queue.values()
        ) {
            response.append(person.getName()).append(" id: ").append(person.getId()).append("\n");
        }

        return response.toString();
    }

    public void setCurrentPlace(int newValue){
        this.currentPlace = newValue;
    }

    // операции с пользовательскими ID
    public void addUserID(Integer userID){
        users.add(userID);
    }
    public void addUserID(ArrayList<Integer> list){
        users.addAll(list);
    }

    public HashSet<Integer> getUsers(){
        return users;
    }

    public void deleteUser(int vkid){
        Iterator<Integer> iter = users.iterator();
        int key = 0;
        while(iter.hasNext()){
            if (iter.next() == vkid){
                users.remove(key);
            }
        }
    }

    // Операции со списками прав доступа

    public void addToReadAccessList(String user){
        readAccessList.add(user);
    }
    public void addToWriteAccessList(String user){
        writeAccessList.add(user);
    }
    public void addToExecuteAccessList(String user){
        executeAccessList.add(user);
    }

    public ArrayList<String> getReadAccessList() {
        return readAccessList;
    }

    public ArrayList<String> getWriteAccessList() {
        return writeAccessList;
    }

    public ArrayList<String> getExecuteAccessList() {
        return executeAccessList;
    }

    public int getIDByVKID(int vkid){
        for (Person person: queue.values()
             ) {
            if (person.getVKID() == (vkid)){
                return person.getId();
            }
        }
        throw new PersonNotFoundException("Персонаж не найден по VKID");
    }

    public boolean isEnded(){
        return queue.isEmpty();
    }
}
