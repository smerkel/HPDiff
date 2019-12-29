/*
**
**    Copyright (C) 2005-now S. Merkel, Univ. Lille, France, 
**    http://merkel.texture.rocks
**
**    This is part of the HPDiff application
**    http://merkel.texture.rocks/HPDiff/
**
**    This program is free software; you can redistribute it and/or modify
**    it under the terms of the GNU General Public License as published by
**    the Free Software Foundation; either version 2 of the License, or
**    (at your option) any later version.
**
**    This program is distributed in the hope that it will be useful,
**    but WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
**    GNU General Public License for more details.
**
**    You should have received a copy of the GNU General Public License
**    along with this program; if not, write to the Free Software
**    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
**
*************************************************************************/

diffHP, a program to help high pressure scientists to deal with their powder diffraction data!
Copyright (C) 2005-now S. Merkel, Univ. Lille, France, http://merkel.texture.rocks

Folders
- java classes: class/
- java source: scr/
- jar archives jar/
- JCPS database database/

To build:
- use 'ant' is the main folder: simply run 'ant' and it should compile

To run:
- main applet: java -jar jar/hpdiff.jar
- phase database management: java -jar jar/manageDB.jar

Phase database
- phase database is in database/phaseDatabase.dat
- if you change it, copy the file "phaseDatabase.dat" into the class/ folder. The application looks for this file.

