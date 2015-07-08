package obj.chr;

public class NPC extends Nonplayer {
	private String text, toScript;
	private boolean isSpeaking = false, noWait = false;

	
	public NPC(float x, float y, float z, String characterType) {
		super(x, y, z, characterType);
	}
	
	
	//UPDATE FUNCTIONS
		public void updateAnimation() {
			updateAnimToHelper();
		}
		
		public void updateAnimToHelper() {
			//npc_animation_tohelper()

			/*
			if(global.currentAction == "npc_tohelper_toplayer" && action == A_TOHELPER) {
			    move_towards_point(parPlayer.x,parPlayer.y,distance_to_point(parPlayer.x,parPlayer.y)/64);
			    parPlayer.speed = 0;
			    if(distance_to_point(parPlayer.x,parPlayer.y) < 32)
			    {
			        global.currentAction = "npc_tohelper_end";
			        speed = 0;
			        z = parPlayer.z;
			        flipDir = parPlayer.flipDir;
			    }
			}

			if(global.currentAction == "npc_tohelper_end" && action == "toHelper")
			{
			    currentHelper = type;
			    with(parHelper)
			        animated_model_set(modelType,currentHelper);

			    parHelper.x = x;
			    parHelper.y = y;
			    parHelper.z = z;
			    parHelper.zSpeed = 0;
			    parHelper.flipDir = flipDir;
			    parHelper.dir = dir;
			    parHelper.sprite = sprite;
			    parHelper.image_index = image_index;
			    
			    parHelper.visible = true;
			    
			    global.currentAction = "";
			    
			    destroy();
			}*/
		}
	
}
	/*public void toPoint(float toX, float toY, float toZ, float toVel, String endScript) {
		action = A_TOPOINT;
  
		goToX = toX;
		goToY = toY;
		goToZ = toZ;
		goToVel = toVel;
		goToScript = endScript; 
		//string_replace_all(string_replace_all(argument5,"/","`"),"~","'");
	}

	public void update(float deltaT) {		
		if(!gotBeh)
		{
		    npc_update_behavior();
		    animated_spritemap_update();
		    animated_model_set(character_get_modeltype(type),type);
		    
		    //if(type == "bombette" || type == "parakarry" || type == "bobomb")
		    //    animated_model_set("double sprite",type);
		    //else if(type == "bowser")
		    //    animated_model_set("bowser");
		}

		npc_animation();

		super.update(deltaT);
		
		if(action = "speaking" && global.currentText == "")
		{
		    action = "wait";
		    if(model != 2)
		        sprite = sprStill;
		    else
		        totSprite = "still";
		}

		//Have NPC Talk
		if(action == "still")
		{
		    speed = 0;
		    if(model != 2)
		        sprite = sprStill;
		    else
		        totSprite = "still";
		}
		else if(action == "speakingSt")
		{
		    if(global.currentAction != "text")
		    {
		        text_add_speech(mySpeech);
		        direction = point_direction(x,y,parPlayer.x,parPlayer.y);
		        speed = 0;
		        moving = 0;
		        action = "speakingFix";
		        
		        parHelper.speed = 0;
		        parHelper.moving = 0;
		    }
		}
		else if(action == "speakingFix")
		    action = "speaking";
		else if(action == "speaking")
		{ 
		    if(model != 2)
		        sprite = sprSpeaking;
		    else
		        totSprite = "speaking";
		        
		    if(!(global.currentLine == 4 && global.endLine))
		    {
		        if(string_char_at(string(global.currentText),global.numText) != " " && string_char_at(string(global.currentText),global.numText) != "^" && string_char_at(string(global.currentText),global.numText) != "")
		        {
		            if(model != 2)
		                image_index -= .25;
		            //else
		            //    headIndex += .25;
		        }
		        else
		            if(model != 2)
		                image_index -= .5;
		            //else
		            //    headIndex += .5;
		    }
		    else
		    {
		        if(model != 2)
		            image_index = 0;
		        else
		            totSprite = "still";
		    }
		}
	}
}*/

		//if(global.currentAction == 'text')
//		    speed = 0;



		//Set Depth
		/*if instance_number(parPlayer) > 0
		{   
		    if point_distance(x,y,parCamera.x,parCamera.y) > point_distance(parPlayer.x,parPlayer.y,parCamera.x,parCamera.y)
		        depth = -999
		    if point_distance(x,y,parCamera.x,parCamera.y) < point_distance(parPlayer.x,parPlayer.y,parCamera.x,parCamera.y)
		        depth = -1001
		}
	}
}*/
