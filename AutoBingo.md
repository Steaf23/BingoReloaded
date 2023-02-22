# Auto Bingo Commands
Bingo Reloaded v1.5.0 adds a set of commands that can be used from the console or by a `bingo.admin` player in-game. The purpose of these commands, as the name suggests, is to remove the middle man and automate bingo games more for server owners that want this plugin to work on its own. 

With Bingo Reloaded 2.0.0 these commands have to be used on a specific world as this update makes multiworld bingo possible.

All commands are tab completable. See the list of commands below for a full list.

Just like the /bingo menu settings, these commands apply to any future games of bingo until the server restarts or the option is overwritten by a new command.

## Commands

- ### `/autobingo <world_name> create <teamsize>`
    Creates a game session in the world named world_name. 
    If the game also takes place in the nether and end, make sure to use the pattern of name_nether and name_the_end where name is the world name.

    Arguments:
  - *Required** `<world_name>`: the name of the bingo world to apply this setting to.
  - *Required** `<teamsize>`: number of players per team from 1 to 64.

- ### `/autobingo <world_name> end`
    Destroys the created session in the world named world_name.

    Arguments:
  - *Required** `<world_name>`: the name of the bingo world to apply this setting to.

- ### `/autobingo <world_name> start <gamemode> <cardsize>`
	Starts the bingo game. A single session/bingo world can only have 1 active game at a time. 

	Arguments:
  - *Required** `<world_name>`: the name of the bingo world to apply this setting to.
  - *Required** `<gamemode>`: can be any of: 
    - 'regular' 
    - 'lockout'
    - 'complete'
    - 'countdown'
  - *Optional** `[cardsize]`: can be any of:
    - '3'
    - '5'

- ### `/autobingo <world_name> end`
	Ends the bingo game. alias for /bingo end.
    
    Arguments:
  - *Required** `<world_name>`: the name of the bingo world to apply this setting to.

- ### `/autobingo <world_name> kit <name>`
	Select the kit to use in the bingo game.

	Arguments:
  - *Required** `<world_name>`: the name of the bingo world to apply this setting to.
  - *Required** `<name>`: can be any of: 
    - 'hardcore' 
    - 'normal'
    - 'overpowered'
    - 'reloaded'
    - ~~'custom'~~ (as of 1.5.0 not yet implemented, defaults to hardcore)

- ### `/autobingo <world_name> effects <effect_name> [true|false]`
	Select the effects to use in the bingo game.

	Arguments:
  - *Required** `<world_name>`: the name of the bingo world to apply this setting to.
  - *Required** `<effect_name>`: can be any of: 
    - 'all' (select all effects)
    - 'none' (select no effects)
    - 'water_breathing'
    - 'night_vision'
    - 'fire_resistance'
    - 'no_fall' (no fall damage)
    - 'card_speed' (speed I when holding card)
  - *Optional** `[true | false]`: (doesn't apply to 'all' and 'none') can be any of:
    - 'false' turn off this effect in the bingo game.
    - 'true' turn on this effect in the bingo game.

- ### `/autobingo <world_name> card <card_name>`
	Select the card to use in the bingo game.

	Arguments:
  - *Required** `<world_name>`: the name of the bingo world to apply this setting to.
  - *Required** `<card_name>`: exact name of the card (i.e. 'default_card')

- ### `/autobingo <world_name> team_max <teamsize>`
	Select the maximum amount of players per team in the bingo game.

	*Important!*: set this option before players are joining teams to have an effect.

    Arguments:
  - *Required** `<world_name>`: the name of the bingo world to apply this setting to.
  - *Required** `<teamsize>`: 

- ### `/autobingo <world_name> duration <minutes>`
	Select the duration of the timer in Countdown Bingo.

	Arguments:
  - *Required** `<world_name>`: the name of the bingo world to apply this setting to.
  - *Required** `<minutes>`: duration in minutes from 1 to 60.


## Example
Example of how you can set up a game using only autobingo commands:

```
/autobingo world create 3

/autobingo world card my_epic_card

/autobingo world effects all
/autobingo world effects no_fall false
/autobingo world effects card_speed false
* the above 3 commands will turn on all effects except no_fall and card_speed *

/autobingo world kit overpowered

/autobingo world duration 20

/autobingo world start countdown 3

```
