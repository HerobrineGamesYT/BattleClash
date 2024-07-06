# BattleClash
A Minecraft minigame featuring both a custom Team Deathmatch and Tower Attack gamemodes with custom classes and some custom mechanics inspired from the mobile game Clash Royale. Development on this project was done on/off throughout 2021.

Dependencies:
- [Mine] HerobrinePVP-CORE (Main core. It's the backbone for all my custom plugins within this setup.)
- [Mine] GameCore (Lots of minigame-related functions, configuration settings, and arena management. It's the backbone for all my minigame plugins within this setup.)
- [3rd Party] NoteBlockAPI (HBPVP-Core Dependency for playing custom NoteBlock themes - i.e win/draw/lose jingles)

Design Notes:
- This game mode is meant to be a fast-paced PVP mode where players can dive in as their favorite Clash Royale character, and use their tactics and abilities to win the game!
- The Original mode is more PVP combat-focused, while Towers mode has a little more strategy, as you need to put constant pressure on the enemy's towers while also trying to work with your teammates to defend your own.
- The Original game mode can be played with as little as 2 players, while at least 4-6 players are recommended for the Towers mode.
- Each of the classes are designed with their own strategies in mind (and also try to keep faithful to their Clash Royale counterparts), for example, the Bandit is more of a fast-paced fighter while the wizard is more of a support class, being able to double jump around the arena and shoot fireballs that can deal area damage to multiple targets.

More information on this game:

Custom Classes:
- Bandit: Fast and sneaky, melee your enemies with a sharp stick, complete with a dash ability!

 ![image](https://user-images.githubusercontent.com/74119793/198856952-2f6d7af5-a29a-471a-bacf-3e6dd5898884.png)


- Wizard: Shoot fireballs at your enemies! Bonus: Can double jump every 7 seconds and has a 25% resistance to melee attacks

![image](https://user-images.githubusercontent.com/74119793/198856960-2d90b3ff-27f7-4543-af6c-f0a3178e87f4.png)


- Knight: A classic. Melee your enemies in your shining gold armor!

![image](https://user-images.githubusercontent.com/74119793/198856967-55a6edc2-bde7-4551-b3a8-438d53df1feb.png)


- Archer: Shoot arrows at your enemies!

![image](https://user-images.githubusercontent.com/74119793/198856973-ac397ff7-1bed-4c18-8e28-d9c5ae93c3bd.png)


- Witch: Shoot magic and summon skeletons to do your bidding!

![image](https://user-images.githubusercontent.com/74119793/198856991-db29df0e-07da-4f89-a6ec-ad7551b18136.png)

- Battle Healer: Heal teammates in a 3 block radius with your attacks!

![image](https://user-images.githubusercontent.com/74119793/198856999-b13a6aa0-2b53-4f13-8eb2-54192d486ecc.png)


- Lumberjack: Attack with your axe and give nearby teammates a "rage effect" [haste and speed] for a few seconds on death!

![image](https://user-images.githubusercontent.com/74119793/198857007-f3cfe39b-614e-4fc3-89c7-6a4abd7a55f1.png)


**Original**: 
![image](https://user-images.githubusercontent.com/74119793/198855263-b5667fea-7e6d-426d-a934-bb2f5052a84f.png)
Use your class items and abilities to fight for your team. The team with the most kills after 5 minutes wins!
![image](https://user-images.githubusercontent.com/74119793/198855797-79efa78a-64ec-4cf6-a120-b8f9e8f498a2.png)

**Towers**: Defend your towers and destroy enemy towers! The team with the most towers standing at the end wins! Destroy the King tower after taking out at least one princess tower for an instant victory!
![image](https://user-images.githubusercontent.com/74119793/198855829-cc40b427-7172-4e65-92c4-095bf501e6fe.png)

This gamemode was more heavily inspired by Clash Royale, and the community seemed to approve of it from the early development stage:
https://www.reddit.com/r/ClashRoyale/comments/poo6po/developing_a_playable_version_of_cr_in_minecraft/ 

Showcase w/ dev notes is available here: https://www.reddit.com/r/ClashRoyale/comments/q097h0/my_cr_in_minecraft_game_is_now_available_for_beta/

Extra mechanics in this mode include:
  - Tower/Cannons: Towers have a set health and a set region. Hitting them within the region will damage them. If you are in the range of a tower's cannon, it will shoot cannonballs at you until you either exit the range or die. If you get too far away from a cannon and a cannonball is still after you, it will be removed. When a tower's health reaches 0, it is considered dead and the opposite team earns a crown. The cannon for that tower is also deactivated, and the "King Tower" (Main Tower) is now activated and is able to be damaged. Its cannons will also start firing at nearby enemies.
  - Tower Hitspeed: Each class deals a certain amount of damage to towers per hit, and have a hitspeed of how fast they can hit the towers to prevent people from spam-clicking them.
  
  
  ![image](https://user-images.githubusercontent.com/74119793/198856890-cb0c48a8-2e01-43be-82c6-73e9e4887c72.png)




