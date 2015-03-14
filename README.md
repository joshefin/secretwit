# What it is

**SecreTwit** is a simple Twitter client with a unique feature – hiding secrets in tweets.

### Hiding in plain sight
Using several steganography techniques, **SecreTwit** enables one to hide information in plain sight – tweets. Only those who know where to look will be able to read it.

### Works anywhere
Since it’s built on Java, it works on all popular platforms – Windows, Linux & Mac OS.

### Inspired by Metro
**SecreTwit** UI is inspired by Metro design language. It has elegant and clean user interface.

# How it works

**SecreTwit** uses several steganography techniques to hide secret message inside a tweet. For the ordinary eye tweet will look just like any other. Only **SecreTwit** users can see hidden message.

### Whitespace steganography
**SecreTwit** conceals message in tweet by appending whitespaces to the end of the tweet. Each bit of a character is represented with a whitespace – 0 is a tab and 1 is an empty space. Web browsers replace multiple whitespaces with a single space so this will be invisible in the browser. Depending on the length of original message, over 100 bits can be used to hide secret message.

### URL steganography
Relying on the fact that people usually won’t look very hard, **SecreTwit** uses fake bit.ly and twitpic.com URLs to hide parts of the message. Those two services are often used on Twitter so they won’t look strange. Part of the message is Base64 encoded and used to make up URL, so this makes 6 bytes per URL available for hiding messages.

### Image steganography
Traditional image steganography technique is used to embed tag within user’s profile image. Tag is later used to determine whether the tweet has a secret message embedded with **SecreTwit**. Steganography method used here is the most common and simplest form of digital steganography called Least Significant Bit (LSB) method. Binary representation of the message that's to be hidden is written into the LSB of the bytes of the image. The overall change to the image is so minor that it can't be seen by the human eye.
