package it.unipd.math.pcd.actors;

/**
 * Created by waltersandon on 08/02/16.
 */


public class ActorSystemImpl extends AbsActorSystem{

    //Method to create only LOCAL actors
    protected ActorRef createActorReference(ActorMode mode){

        if(mode == ActorMode.LOCAL)
            return new ActorRefImpl<>(this);

        else
            throw new IllegalArgumentException();
    }

}
