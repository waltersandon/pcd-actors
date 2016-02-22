package it.unipd.math.pcd.actors;
import it.unipd.math.pcd.actors.exceptions.NoSuchActorException;

/**
 * Created by waltersandon on 18/02/16.
 */
public class ActorRefImpl<T extends Message> implements ActorRef<T> {

    //Refers to ActorSystem and It serves to get the Action from a ActoRef
    private final ActorSystemImpl system;

    public ActorRefImpl(ActorSystemImpl asi){
        this.system=asi;
    }


    //Override compareTo to compare the elements into hashMap
    public int compareTo(ActorRef a){
        if (this == a)
            return 0;
        return -1;
    }

    //Method that is used to send a message to Actor
    @Override
    public void send(T message, ActorRef to) throws NoSuchActorException{
        AbsActor aux = (AbsActor) system.search(to);
        
        if(aux != null && !aux.getFlag())
            aux.insertMessage(message, this);//into AbsActor
        else
            throw new NoSuchActorException();
    }

}
