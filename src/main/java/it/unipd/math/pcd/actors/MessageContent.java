package it.unipd.math.pcd.actors;

/**
 * Created by waltersandon on 05/02/16.
 */
public class MessageContent<T extends Message> {

    private T message;
    private ActorRef<? extends Message> sender;

    public MessageContent(T msg, ActorRef<? extends Message> sender){

        this.message = msg;
        this.sender = sender;
    }

    //getter methods

    public ActorRef<? extends Message> getActorRef(){
        return sender;
    }

    public T getMessage(){
        return message;
    }


    //setter methods
    public void setActorRef(ActorRef<? extends Message> newActorRef){

        sender = newActorRef;
    }

    public void setMessage(T newMessage){

        message = newMessage;
    }
}


