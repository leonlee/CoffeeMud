package com.planet_ink.coffee_mud.Abilities.Druid;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.collections.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;


import java.util.*;

/* 
   Copyright 2000-2011 Bo Zimmerman

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

@SuppressWarnings("unchecked")
public class Chant_DistantIngrowth extends Chant
{
	public String ID() { return "Chant_DistantIngrowth"; }
	public String name(){ return "Distant Ingrowth";}
	public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTGROWTH;}
	public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	protected int canAffectCode(){return 0;}
	protected int canTargetCode(){return CAN_ROOMS;}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{

		if(commands.size()<1)
		{
			mob.tell("Create growth where?");
			return false;
		}

		String areaName=CMParms.combine(commands,0).trim().toUpperCase();
		Room anyRoom=null;
		Room newRoom=null;
		try
		{
			List<Room> rooms=CMLib.map().findRooms(CMLib.map().rooms(), mob, areaName, true, 10);
			for(Room R : rooms)
			{
				anyRoom=R;
				if((R.domainType()==Room.DOMAIN_INDOORS_STONE)
				||(R.domainType()==Room.DOMAIN_INDOORS_WOOD))
				{
				    newRoom=R;
				    break;
				}
			}
	    }catch(NoSuchElementException e){}

		if(newRoom==null)
		{
			if(anyRoom==null)
				mob.tell("You don't know of a place called '"+CMParms.combine(commands,0)+"'.");
			else
			if(anyRoom.domainType()==Room.DOMAIN_INDOORS_CAVE)
				mob.tell("There IS such a place, but its in a cave where fungus rule, so your magic would fail.");
			else
				mob.tell("There IS such a place, but its not in a building, so your magic would fail.");
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),"^S<S-NAME> chant(s) about a far away place.^?");
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				Item newItem=null;
				if((newRoom.domainType()==Room.DOMAIN_OUTDOORS_UNDERWATER)
				||(newRoom.domainType()==Room.DOMAIN_OUTDOORS_WATERSURFACE))
					newItem=Chant_SummonSeaweed.buildSeaweed(mob,newRoom);
				else
				if((newRoom.domainType()==Room.DOMAIN_INDOORS_STONE)
				||(newRoom.domainType()==Room.DOMAIN_INDOORS_WOOD))
					newItem=Chant_SummonHouseplant.buildHouseplant(mob,newRoom);
				else
					newItem=Chant_SummonPlants.buildPlant(mob,newRoom);
				mob.tell("You feel a distant connection with "+newItem.name());
			}
		}
		else
			beneficialWordsFizzle(mob,null,"<S-NAME> chant(s) about a far away place, but the magic fades.");


		// return whether it worked
		return success;
	}
}
