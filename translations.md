# Translations
So, you want to add a translation to Bingo Reloaded? Please read the following information to make it as simple as possible for you :D

## Steps:
- ### Navigate to your plugins folder in the server, then go to `BingoReloaded/languages`.
- ### Copy and paste the `en_us.yml` file giving it the name of the language.
- ### Open this file and change the text in "" to what's appropriate!
- ### Save the file. To test your translation, go to `config/yml` and set the `language` property to teh name of the file you created.
- ### Reload the server, Bingo Reloaded should now use your translation file!
- ### If you are satisfied with your translation, be sure to send it to me on my discord server so it can be included for everyone to use in future versions!

## Additional Features:
- ### Any time you see an argument like {0}, this will be filled in by the game.
   - If your language's grammer requires you to switch around the arguments, you can do so by reordering the numbers, to use an argument multiple times, just use the same argument number multiple times. You can also omit arguments if you wish by just not using a number, but this just means you are giving less information to the player...
- ### Add colors and text modifiers anywhere!
   - Use § or & in combination with minecrafts color codes (which can be found [here](https://htmlcolorcodes.com/minecraft-color-codes/)) like you would for normal colored text used in other plugins.
   - If you want to use the & in the actual text, you can escape the color parsing by writing a double &&.
   - Bingo reloaded supports Hex colors, meaning you can use any color from the rainbow and back in your translation. To make it somewhat easier for you to use hex colors, use this notation `{#000000}` where the numbers are the color in hexadecimal. If you are unsure how to create hex colors, use [google](https://g.co/kgs/Yd7B1w) and copy the code that's under `HEX`.
   For example you can write `{#ff0000}Hey, I am red text!` to write something in eye-blindingly red. Hex codes can be used in combination with text modifiers like making text bold. This works in the same way as using chat colors; Example: `{#ff0000}&lHey, I am red text!` will write the same as above, but in **bold** (notice the `&l`).
- ### PlacholderAPI strings can be used.
   - Use "%" symbols to wrap placeholders as "{}" is already being used for arguments by the translation file).
   - This feature has not been tested extensively, so please report any issues you come across whilst trying to use Placeholder API in combination with the translation files.

## Tips:
- ### You can look at en_us.yml and nl.yml to get an idea of how the translation system works.
