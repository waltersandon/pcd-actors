/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Riccardo Cardin
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * <p/>
 * Please, insert description here.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */

/**
 * Please, insert description here.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
package it.unipd.math.pcd.actors;

import it.unipd.math.pcd.actors.exceptions.NoSuchActorException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




/**
 * A map-based implementation of the actor system.
 *
 * @author Walter Sandon
 * @version 1.1
 * @since 1.1
 */
public abstract class AbsActorSystem implements ActorSystem {



     //Threadpool of actors that creates Thread as needed

    private ExecutorService executor = Executors.newCachedThreadPool();


    //Associates every Actor created with an identifier.
    private Map<ActorRef<?>, AbsActor<? extends Message>> actors;

    //I need to implement AbsActorSystem with a concrete map as a HashMap
    public AbsActorSystem(){
        actors = new ConcurrentHashMap<>();
    }

    @Override
    public ActorRef<? extends Message> actorOf(final Class<? extends Actor>  actor, final ActorMode mode) {

        // ActorRef instance
        ActorRef<?> reference;
        try {
            // Create the reference to the actor
            reference = this.createActorReference(mode);
            // Create the new instance of the actor
            Actor actorInstance = ((AbsActor) actor.newInstance()).setSelf(reference);
            // Associate the reference to the actor
            actors.put(reference,(AbsActor) actorInstance);
            //Put a actor into a threadpool
            executor.execute((Runnable) actorInstance);

        } catch (InstantiationException | IllegalAccessException e) {
            throw new NoSuchActorException(e);
        }
        return reference;
    }



    @Override
    public ActorRef<? extends Message> actorOf(Class<? extends Actor> actor) {
        return this.actorOf(actor, ActorMode.LOCAL);
    }

    protected abstract ActorRef createActorReference(ActorMode mode);//da implementare in AbsActrorSystemConcrete




    protected  <T extends Message> AbsActor<T> search(ActorRef<?> toSearch) throws NoSuchActorException {

        Actor toS = actors.get(toSearch);
        if (toS == null)
            throw new NoSuchActorException();
        return (AbsActor<T>) toS;
    }

    Map<ActorRef<?>, AbsActor<? extends Message>> getMap(){

        return actors;
    }

    //Search the actor to stop and interrupt this
    @Override
    public void stop(final ActorRef<?> actorToStop) {
        ((AbsActor) search(actorToStop)).interrupt();
        actors.remove(actorToStop);
    }

    //stop all the actors that are in the system
    @Override
    public void stop() {
        for (Map.Entry<ActorRef<? extends Message>, AbsActor<? extends Message>> actor : actors.entrySet()) {
            actor.getValue().interrupt();
            actors.remove(actor);
        }
    }
}