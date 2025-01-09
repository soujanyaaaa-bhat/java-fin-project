**please note no game interface is made**
**it only includes a simple scanner to choose options and play in terminal**
made a prisoner's dilemma game like simulation where player and Aiplayer(like) compete in a series of 5 rounds, allowing for check of defect or cooperate.
this was employed using concepts like abstract class i.e player moves and opponent moves stored
a game logic where points are alotted to each move.
inheritance extending player class for human player and AIplayer
list collections of string type is used for storing moves
the game logic is implemented in runnable interface 
and the scores are stored in the database.
a prepared statement (pre-compiled) is used to avoid sql injection attacks.
threads are used to run the game for 5 rounds.
jdbc is then used to store the scores of ai and player

a java frame is built using jswing, where user can click on two buttons namely, for welcome, and about game.
user can input details like name, contact, age and its displayed on jtable.
when not entered a value, message is shown.
values can be selected to be deleted.
clear option also exists.
