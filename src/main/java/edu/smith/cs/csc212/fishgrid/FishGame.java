package edu.smith.cs.csc212.fishgrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.awt.Color;

/**
 * This class manages our model of gameplay: missing and found fish, etc.
 * @author jfoley
 *
 */
public class FishGame {
	/**
	 * This is the world in which the fish are missing. (It's mostly a List!).
	 */
	World world;
	/**
	 * The player (a Fish.COLORS[0]-colored fish) goes seeking their friends.
	 */
	Fish player;
	/**
	 * The home location.
	 */
	FishHome home;
	/**
	 * These are the missing fish!
	 */
	List<Fish> missing;
	
	/**
	 * These are fish we've found!
	 */
	List<Fish> found;

	List<Fish> homeList;
	List<FishFood> fishFood;

	int fishCount;
	
	/**
	 * Number of steps!
	 */
	int stepsTaken;
	
	/**
	 * Score!
	 */
	int score;
	
	/**
	 * Create a FishGame of a particular size.
	 * @param w how wide is the grid?
	 * @param h how tall is the grid?
	 */
	public FishGame(int w, int h) {
		world = new World(w, h);
		
		missing = new ArrayList<Fish>();
		found = new ArrayList<Fish>();
		homeList = new ArrayList<Fish>();
		fishFood = new ArrayList<FishFood>();
		fishCount = Fish.COLORS.length - 1;
		
		// Add a home!
		home = world.insertFishHome();
		
		// Generate some more rocks!
		int NUM_ROCKS = 10;
		int NUM_FALLINGROCK = 5;
		for (int i=0; i<NUM_FALLINGROCK; i++) {
            world.insertFallingRockRandomly();
        }
		// Make 5 into a constant, so it's easier to find & change.
		for (int i=0; i<NUM_ROCKS; i++) {
			world.insertRockRandomly();
		}
		
		// Make the snail!
		world.insertSnailRandomly();
		
		// Make the player out of the 0th fish color.
		player = new Fish(0, world);
		// Start the player at "home".
		player.setPosition(home.getX(), home.getY());
		player.markAsPlayer();
		world.register(player);
		
		// Generate fish of all the colors but the first into the "missing" List.
		for (int ft = 1; ft < Fish.COLORS.length; ft++) {
			Fish friend = world.insertFishRandomly(ft);
			missing.add(friend);
		}
	}
	
	
	/**
	 * How we tell if the game is over: if missingFishLeft() == 0.
	 * @return the size of the missing list.
	 */
	public int missingFishLeft() {
		return missing.size();
	}
	
	/**
	 * This method is how the Main app tells whether we're done.
	 * @return true if the player has won (or maybe lost?).
	 */
	public boolean gameOver() {
		// if (missing.isEmpty()) {
		// 	player.setPosition(home.getX(),home.getY());
		// 	return true;
		// }
		if (homeList.size() == fishCount) {
			player.setPosition(home.getX(),home.getY());
			return true;
		}
		return false;
	}

	/**
	 * Update positions of everything (the user has just pressed a button).
	 */
	public void step() {
		// Keep track of how long the game has run.
		this.stepsTaken += 1;
				
		// These are all the objects in the world in the same cell as the player.
		List<WorldObject> overlap = this.player.findSameCell();
		List<WorldObject> dayu = home.findSameCell();
		
		for (int i = 0; i < fishFood.size(); i++) {
			List<WorldObject> hungrig = this.fishFood.get(i).findSameCell();
			for (WorldObject wo : hungrig) {
				if (wo.isPlayer()) {
                    this.score+=2;
                    world.remove(fishFood.get(i));
					fishFood.remove(fishFood.get(i));
				} else if (missing.contains(wo)|| found.contains(wo) || (wo instanceof Snail)){
                    world.remove(fishFood.get(i));
					fishFood.remove(fishFood.get(i));
				}
			}
		}


		Random rand = ThreadLocalRandom.current();
		// Dayu is a Chinese mythology character that go home for three times! 
		// WikiPedia : Yu the Great

		if (rand.nextDouble() < .1) {
            fishFood.add(world.insertFoodRandomly());
        }

		if (stepsTaken > 25) {
			if (found.size() > 3){
				if (rand.nextDouble() < 0.2) {
					missing.add(found.get(found.size()-1));
					found.remove(found.get(found.size()-1));
				}
			}
		}

		for (WorldObject wo : dayu) {

            if(missing.contains(wo)){
                homeList.add((Fish)wo);
                world.remove(wo);
                missing.remove(wo);
			} else if (wo.isPlayer()) {
				while (found.size()!=0) {
					Fish fish = found.get(0);
                    world.remove(fish);
                    homeList.add(fish);
                    found.remove(fish);
				}
				//!!!! OH NO IT DON'T WORK SINCE SIZE CHANGES     TAT
                // for(int i = 0; i < found.size(); i++) {
                //     Fish fish = found.get(i);
                //     world.remove(fish);
                //     homeList.add(fish);
                //     found.remove(fish);
                // }
            }
        }
		// The player is there, too, let's skip them.
		overlap.remove(this.player);
		
		// If we find a fish, remove it from missing.
		for (WorldObject wo : overlap) {
			// It is missing if it's in our missing list.
			if (missing.contains(wo)) {
				// Remove this fish from the missing list.
				missing.remove(wo);
				
				// add to found instead! (So we see objectsFollow work!)

				found.add((Fish)wo);
				// Remove from world.
				//world.remove(wo);
				
				// Increase score when you find a fish!
				if (((Fish) wo).color() == Color.white) {
					score += 100;
				} else {
					score += 10;
				}
			}

			
		}
		
		// Make sure missing fish *do* something.
		wanderMissingFish();
		// When fish get added to "found" they will follow the player around.
		World.objectsFollow(player, found);
		// Step any world-objects that run themselves.
		world.stepAll();
	}
	
	/**
	 * Call moveRandomly() on all of the missing fish to make them seem alive.
	 */
	private void wanderMissingFish() {
		Random rand = ThreadLocalRandom.current();
		for (Fish lost : missing) {
			// 30% of the time, lost fish move randomly.
			if (lost.fastScared) {
				if (rand.nextDouble() < 0.8) {
					lost.moveRandomly();
				}
			} else {
				if (rand.nextDouble() < 0.3) {
					lost.moveRandomly();
				}
			}
		}
	}

	/**
	 * This gets a click on the grid. We want it to destroy rocks that ruin the game.
	 * @param x - the x-tile.
	 * @param y - the y-tile.
	 */
	public void click(int x, int y) {
		// use this print to debug your World.canSwim changes!
		//System.out.println("Clicked on: "+x+","+y+ " world.canSwim(player,...)="+world.canSwim(player, x, y));
		List<WorldObject> atPoint = world.find(x, y);
		// Allow the user to click and remove rocks.
		for (int i=0; i < atPoint.size(); i++) {
			WorldObject wo = atPoint.get(i);
			if (wo instanceof Rock||wo instanceof FallingRock) {
				world.remove(wo);
			}
		}

	}
	
}
