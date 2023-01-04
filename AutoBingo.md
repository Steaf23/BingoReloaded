# Auto Bingo Commands
Bingo Reloaded v1.5.0 adds a set of commands that can be used from the console or by a `bingo.admin` player in-game. The purpose of these commands, as the name suggests, is to remove the middle man and automate bingo games more for server owners that want this plugin to work on it's own. 

These commands alone aren't able to elevate this plugin to make it usable on a public server. This will be adressed in a future update.

All commands are tab completable. See the list of commands below for a full list.

Just like the /bingo menu settings, these commands apply to any future games of bingo until the server restarts or the option is overwritten by a new command.

## Commands

- ### `/autobingo start <gamemode> <cardsize>`
	Starts the bingo game.

	Arguments:
  - *Required** `<gamemode>`: can be any of: 
    - 'regular' 
    - 'lockout'
    - 'complete'
    - 'countdown'
  - *Optional** `[cardsize]`: can be any of:
    - '3'
    - '5'

- ### `/autobingo end`
	Ends the bingo game. alias for /bingo end.


- ### `/autobingo kit <name>`
	Select the kit to use in the bingo game.

	Arguments:
  - *Required** `<name>`: can be any of: 
    - 'hardcore' 
    - 'normal'
    - 'overpowered'
    - 'reloaded'
    - ~~'custom'~~ (as of 1.5.0 not yet implemented, defaults to hardcore)

- ### `/autobingo effects <effect_name> [true|false]`
	Select the effects to use in the bingo game.

	Arguments:
  - *Required** `<effect_name>`: can be any of: 
    - 'all' (select all effects)
    - 'none' (select no effects)
    - 'water_breathing'
    - 'night_vision'
    - 'fire_resistance'
    - 'no_fall' (no fall damage)
    - 'card_speed' (speed I when holding card)
  - *Optional** `[true | false]`: (doesn't apply to 'all' and 'none' can be any of:
    - 'false' turn off this effect in the bingo game.
    - 'true' turn on this effect in the bingo game.

- ### `/autobingo card <card_name>`
	Select the card to use in the bingo game.

	Arguments:
  - *Required** `<card_name>`: exact name of the card (i.e. 'default_card')

- ### `/autobingo team_max <teamsize>`
	Select the maximum amount of players per team in the bingo game.

	*Important!*: set this option before players are joining teams to have an effect.

	Arguments:
  - *Required** `<teamsize>`: number of players per team from 1 to 64.

- ### `/autobingo duration <minutes>`
	Select the duration of the timer in Countdown Bingo.

	Arguments:
  - *Required** `<minutes>`: duration in minutes from 1 to 60.


## Example
Example of how you can set up a game using only autobingo commands:

```
/autobingo team_max 3

/autobingo card my_epic_card

/autobingo effects all
/autobingo effects no_fall false
/autobingo effects card_speed false
* the above 3 commands will turn on all effects except no_fall and card_speed *

/autobingo kit overpowered

/autobingo duration 20

/autobingo start countdown 3

```
