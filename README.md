# **Bingo**- *Reloaded* 1.2.0
A Minecraft 1.18/1.19+ Spigot Plug-in, take a look at the card viewer!
![card](https://user-images.githubusercontent.com/21062141/155626593-fa054f3c-98c2-4e70-9ef6-404153224155.png)
***
Thanks for looking at my Bingo Plug-in!

This plug-in is written for Minecraft version 1.18 and up, inspired by https://www.spigotmc.org/resources/bingo-2-2-for-1-18.92134/.

For now atleast, this plug-in is intended for use in closed groups, i.e. with your friends on your own server. 

I created a Discord server where you can post your concerns or requests, join it [here](https://discord.gg/AzZNxPRNPf)

To get started, use the `/bingo` command, after which a menu will open that you can use to join or leave the game. To actually start the game you have to be OP and use the command `/bingo start`. Admins also get access to the settings in `/bingo`. To end a game prematurely, the `/bingo end` command can be used. Finally if during the game your Bingo card has been lost, use `/bingo getcard` to get it back!
When playing the game you can complete items from the card by throwing them out of your inventory (using q when holding an item or dragging them with the mouse outisde of the inventory slots).

# Features:
- Play in ***3*** different gamemodes!
  - Regular
  - Lockout
  - Complete-all
- Choose between ***4*** seperate kits!
  - Hardcore
  - Normal
  - Overpowered
  - Reloaded
- Choose between a card size of 3x3 or 5x5 for quick or longer games!
- Choose between 16 teams, based on Minecraft's beloved colors!
- Create your own BingoCards using the card editor! By default it will use a seperate list (that can be edited if you wanted to), containing 150 unique items from the game.
- Due to it's teleportation at the start, it's a great way to explore the new terrain generation that 1.18 brought to the game!
- Resume games that have been paused by the server or plug-in crashing or after having been reloaded plug-in due to the automatic card recovery system!
- Team Chat! you can use `/btc` to toggle team chat on or off when a game is in progress.
- Several Effects! You get constant water breathing, fire resistance and night vision. You get speed 1 when holding the bingocard in your hand.
![options](https://user-images.githubusercontent.com/21062141/155626604-a46a900a-156b-4fd5-93ff-19b3197b5bab.png)

## Play bingo in 3 Different gamemodes
This plugin has 3 seperate ways to play bingo with your friends.
- **Regular**, this is the bingo everyone knows about. Be the first to complete 1 line going horizontally, vertically or diagonally to win the game!
- **Lockout**. Be the first to complete the majority of the card to win. But there is a catch, items that other teams have gathered cannot be used again!
- **Complete-All**. Be the first one to complete all items on the card. Complete-all games can be pretty long if you have made a hard card!


## Choose betreen 4 seperate kits
The game host can choose a kit that will be used by all participants.
- **Hardcore**. Some people might call it the classic or the only way to play bingo. This kit gives you nothing but the Card!
- **Normal**. A slightly more forgiving kit giving you iron tools and some food.
- **Overpowered**. Your tools are upgraded to Netherite! You also get access to the Go-Up-Wand!
- **Reloaded**. Same as overpowered but you also get an Elytra that (practically) doesn't break!


### The Go-Up-Wand
This items allows the user to teleport themselves high into the sky when right-clicked. this item can be used to get a better view of your current area or to get out of caves quickly. But be warned, there is a 5 second cooldown on using it! This item makes firework rockets obsolete when using an elytra.


## Create your own BingoCards using the card editor!
This plugin includes a way to easily create new cards without messing around in the configuration files. 
Using the commands `/card` and `/itemlist` you can assemble your own card configurations and then directly use them in the next game!

When creating a card using `/card create <name>` a menu will come up where you can add item lists to the card. 
![cardedit](https://user-images.githubusercontent.com/21062141/155626597-0507e4eb-b342-4c21-8dc6-f9095ca7b13b.png)
Each list has a maximum number of times an item from it can appear on the card. This will allow you to have varying rarity between the item lists you create. 
![itemlistedit](https://user-images.githubusercontent.com/21062141/155626602-b68f68e9-3bcb-4612-9feb-979e5bd3ddba.png)
If all the numbers on the added itemlists don't add up to amount of slots on the card (i.e. 25 for a 5x5 card) the rest will be filled up from items in the default list.

To add new Item lists, use the `/itemlist create <name>` command. In the list editor you can pick from every block in the game with one exception being stained glass panes. Make sure the items you select will be available during the game or no one can ever win :P.
![itemlist](https://user-images.githubusercontent.com/21062141/155626599-ff38778b-f75a-4768-97b9-03dcc904434d.png)

***

**NOTE(3):** *I am a professional programmer, but this is the first time I have made a Spigot plug-in on my own. If you are a technical reader and notice something out of practise in my code or if you want to contribute, feel free to contact me!*
