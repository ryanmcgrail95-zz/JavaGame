import java.util.ArrayList;
import java.util.List;
import ds.vec4;
import interfaces.Useable;
import obj.itm.Item;
import paper.ActorPM;
import object.environment.ResourceMine;
import object.primitive.Positionable;

public class Task {
		public static final byte 
			T_MOVETO = 0,
			T_USE = 1, 
			T_RUNSTORE = 2,
			T_MOVENEAR = 3, T_CHASE = 4, T_FACE = 5, T_TALKTO = 6,
			T_TALKING = 7;
	
		public enum Type {
			STAND_STILL,
			STAND_PACE, STAND_PACE_MOVE, 
			MOVE_TO, 
			MOVE_NEAR, 
			USE, 
			RUN_STORE 
		}
		
		private byte type;
		private vec4 toPoint;
		private Item myItem, targetItem;
		private ActorPM myActor, targetActor;
		private Useable targetUseable;
		private float targetValue;
		private ResourceMine targetResourceMine;

		public Task(byte type) {
			this.type = type;
		}

		public Task(byte type, Actor targetActor) {
			this.type = type;
			this.targetActor = targetActor;
		}

		public Task(byte type, Actor myActor, ResourceMine targetResourceMine) {
			this.type = type;
			this.myActor = myActor;
			this.targetResourceMine = targetResourceMine;
		}

		public Task(byte type, Actor myActor, Useable targetUseable) {
			this.type = type;
			this.myActor = myActor;
			this.targetUseable = targetUseable;
		}

		public Task(byte type, vec4 toPoint, Item myItem, Item targetItem,
				Actor myActor, Actor targetActor, Useable targetUseable,
				float targetValue) {
			this.type = type;
			this.toPoint = toPoint;
			this.myItem = myItem;
			this.targetItem = targetItem;
			this.myActor = myActor;
			this.targetActor = targetActor;
			this.targetUseable = targetUseable;
			this.targetValue = targetValue;
		}

		public void perform() {
			boolean done = false;

			if (type == T_MOVETO) {
				if (move(toPoint.get(3) == 1, toPoint.xy())) {
					stop();
					done = true;
				}
			} else if (type == T_MOVENEAR) {
				if (move(toPoint.get(3) == 1, toPoint.xy())
						|| calcPtDis(toPoint.x(), toPoint.y()) < targetValue) {
					stop();
					done = true;
				}
			} else if (type == T_USE) {
				targetUseable.use(myActor);
				done = true;
			} else if (type == T_CHASE) {
				if (move(true, targetActor))
					done = true;
			} else if (type == T_FACE) {
				if (face(targetActor))
					done = true;
			} else if (type == T_RESOURCEMINING) {
				if (targetResourceMine.mine(myActor))
					done = true;
			}

			if (done)
				taskList.remove(this);
		}

		public byte getType() {
			return type;
		}
	}
