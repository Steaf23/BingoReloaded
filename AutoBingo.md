# Auto Bingo Commands (V 2.0.0)
Bingo Reloaded v1.5.0 adds a set of commands that can be used from the console or by a `bingo.admin` player in-game. The purpose of these commands, as the name suggests, is to remove the middle man and automate bingo games more for server owners that want this plugin to work on its own. 

All commands are tab completable. See the list of commands below for a full list.

Just like the /bingo menu settings, these commands apply to any future games of bingo until the server restarts or the option is overwritten by a new command.

## Commands

- ### `/autobingo start`
	Starts the bingo game. A single session/bingo world can only have 1 active game at a time. 

	Arguments: -

- ### `/autobingo end`
	Ends the bingo game. alias for /bingo end.
    
    Arguments: -

- ### `/autobingo gamemode <gamemode>`
  Set the gamemode to use in the bingo game
  
  Arguments:
  - *Required** `<gamemode>`: can be any of: 
    - 'regular' 
    - 'lockout'
    - 'complete'
    - 'countdown'
  - *Required** `<cardsize>`: can be any of:
    - '3'
    - '5'

- ### `/autobingo kit <name>`
	Select the kit to use in the bingo game.

	Arguments:
  - *Required** `<name>`: can be any of: 
    - 'hardcore' 
    - 'normal'
    - 'overpowered'
    - 'reloaded'
    - 'custom_1' through 'custom_5' (if undefined defaults to hardcore)

- ### `/autobingo effects <effect_name | all | none> [true | false]`
	Select the effects to use in the bingo game.

	Arguments:
  - *Required** `<effect_name | all | none>`: can be any of: 
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

- ### `/autobingo card <card_name> [card_seed]`
	Select the card to use in the bingo game.

	Arguments:
  - *Required** `<card_name>`: exact name of the card (i.e. 'default_card')
  - *Optional** `[card_seed]`: select a card seed, with the same seed, and card_name, the same exact card tasks will also be generated.
    - Default value is 0, meaning no seed is used.

- ### `/autobingo teamsize <teamsize>`
	Select the maximum amount of players per team in the bingo game.

	**Note**: This will remove all players from existing teams when the new value is smaller than the old value.

    Arguments:
  - *Required** `<teamsize>`: 

- ### `/autobingo countdown <true | false>`
  Enable countdown mode, set the duration of this gamemode using /autobingo duration.

  Arguments:
  - *Required** `<true | false>`: enable countdown mode when true.

- ### `/autobingo duration <minutes>`
	Select the duration of the timer in countdown mode. This command has no effect when countdown is set to false.

	Arguments:
  - *Required** `<minutes>`: duration in minutes from 1 to 60.

- ### `/autobingo team <player_name> <team_name | none | auto>`
  Add the given player to the given team, using none will remove the player from any team they are on.

  Arguments:
  - *Required** `<player_name>`: name of the player to select a team for.
  - *Required** `<team_name | none | auto>`: the name / id of the team, found in `/bingo teams` for custom made teams
    - Use 'none' to remove the player from all teams
    - Use 'auto' to allow the plugin to pick the best team for the player when the game starts.

- ### `/autobingo preset <load | save | remove> <preset_name>`
  Select a settings preset to use for the next game.
  - *Required** `<load | save | remove>`: 
    - Specify whether to load, save, or remove a preset.
    - **Note**: when loading a preset, all previously selected settings for this game will be overwritten by the selected preset.
  - *Required** `<preset name>`: The name of the preset.
  
- ### `/autobingo playerdata <load | save | remove> <player_name>`
	Manage player data manually, should be used if the config options don't provide enough freedom in how you want to use player data.
  (or when you need to recover data in certain situations).

	Arguments:
  - *Required** `<load | save | remove>`: Specify whether to load, save, or remove player data.
  - *Required** `<player_name>`: The name of the player whose data will be loaded, saved, or removed.

- ### `/autobingo vote <player_name> <vote_category> <vote_for>`
	Place a vote for a specific category in the autobingo game, posing as a player.
  This is mainly used if you need a custom voting system, which may involve ranks and different permissions etc.. 

	Arguments:
  - *Required** `<player_name>`: The name of the player to cast a vote from.
  - *Required** `<vote_category>`: can be any of:
      - 'kits'
      - 'gamemodes'
      - 'cards'
  - *Required** `<vote_for>`: The specific item within the vote category to vote for.

## Example
Example of how you can set up a game using only autobingo commands:

```
/autobingo card my_epic_card

/autobingo effects all
/autobingo effects no_fall false
/autobingo effects card_speed false
* the above 3 commands will turn on all effects except no_fall and card_speed *

/autobingo kit overpowered

/autobingo countdown true
/autobingo duration 20

/autobingo gamemode lockout 5
/autobingo start

```
