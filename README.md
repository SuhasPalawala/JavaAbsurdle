# JavaAbsurdle
This program is written in Java and is intended to replicate the game Absurdle, a game that works somewhat like Wordle,
but has some key differences. Like Wordle, the user can guess words and receive hints as to what
the final word could be through a pattern of colored emojis. But, Absurdle works differently in that 
it does not select a single word initially. It instead filters through a dictionary to prolong the game
as much as possible. Every turn, Absurdle compares potential options for what the final word could be
by determining which pattern of emojis displayed have the most potential words associated with them. In this version of Absurdle, you may only input and play with words that are 5 letters long.