lancu
=====

The Language Curator - a tool to organize constructed languages


WARNING:
--------

At this stage lancu has to be considered to be ALPHA quality!
There is currently no documentation how the program works since whatever I write down will most likly be out of date a few days later.
I will write a documentation when the development has reached a stage where it might make sense to use it. 


About lancu
-----------

lancu (LANGuage CUrator) is a program that I started with the goal that it would help me manage my conlangs (constracted languages).


Here is a short breakdown of the features that are more or less implemented at this point:

* create lanuages
* phoneme list per language
* syllable structure per language
* free definition of word class/word categories (e.g. noun, verb) per language 
* free definition of inflections (e.g. gender, tense) per word class 
* free definition of inflection states (e.g. present, past) per inflection 
* free definition of rules how words change if they are regular (e.g. "past gets '-ed' at the end") per inflection 
* morpheme list
* word list, where words can be composed out of morphemes or "free text"
* automatic generation of all possible word forms based on the defined word class and its rules (NOT YET DISPLAYED IN THE FRONTEND)
* check if all morphemes, words and there word forms are valid based on the phoneme-list and the syllable structure (NOT YET DISPLAYED IN THE FRONTEND)


These are the ***features***  I plan to implement next:

* possibility to overwrite the generated word forms (aka define irregular words)
* categories for phonemes (e.g. vowels, fricatives)



Other stuff thats currently on my list:

* use DockingWindows for all modules
* replace the current rollback/commit feature with a undo/redo based one
