/*
 * Copyright (C) 1996 Emanuel Borsboom <manny@zerius.victoria.bc.ca>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package zrs.synth;

import java.awt.*;
import java.util.*;
import java.io.*;
import zrs.synthgen.*;
import zrs.wave.*;
import zrs.ui.*;

class SynthCanvas extends Canvas implements PropertiesObserver {

  SynthWindow pa;

  static final Color BACKGROUND_COLOR = Color.white;
  static final int dragThreshold = 1;

  int samplingRate = 8000;
  double time = 1.0;

  Dimension prefSize = new Dimension (384, 512);

  Vector generators = new Vector();
  Vector connections = new Vector();

  Point dragStart;
  boolean dragged;

  boolean newGeneratorFlag = false;
  GeneratorBox newGeneratorGen;

  boolean moveOriginFlag = false;

  boolean selectBoxFlag = false;
  boolean selectBoxAddFlag;

  boolean moveGeneratorFlag = false;
  GeneratorBox moveGeneratorGens[];
  GeneratorBox moveGeneratorGen;
  boolean moveGeneratorSelectedFlag;

  boolean selectDestFlag = false;
  GeneratorBox selectDestSource;

  RubberBand rubbers[];

  SynthProperties propertiesDialog;

  SynthCanvas (SynthWindow pa) {
    this.pa = pa;
    setBackground (BACKGROUND_COLOR);
  }

  void synthesize() {
    int sampleCount = (int)(samplingRate * time);
    Synthesizer synth = new Synthesizer (samplingRate);
    GeneratorBox output = null;

    try {

      Enumeration e = generators.elements();
      while (e.hasMoreElements()) {
        GeneratorBox gen = (GeneratorBox) e.nextElement();
        gen.newGenerator (synth);
        if (gen instanceof OutputBox)
          output = gen;
      }

      if (output == null) throw new SynthIfException ("No output specified.");

      e = generators.elements();
      while (e.hasMoreElements()) {
        GeneratorBox gen = (GeneratorBox) e.nextElement();
        gen.connectGenerator (synth);
      }

      synth.setOutput (((Input)output.inputs.elementAt (0)).getGenerator());

      new SynthesizeThread (pa, synth, samplingRate, sampleCount).start();
    }
    catch (Exception e) { new ExceptionDialog (pa, e).show(); }
  }

  public Dimension minimumSize() {
    return prefSize;
  }

  public Dimension preferredSize() {
    return minimumSize();
  }

  GeneratorBox inGenerator (int x, int y) {
    for (int i = generators.size() - 1; i >= 0; i--) {
      if (((GeneratorBox)generators.elementAt (i)).inside (x, y)) {
	return (GeneratorBox)generators.elementAt (i);
      }
    }
    return null;
  }

  Connection inConnection (int x, int y) {
    for (int i = connections.size() - 1; i >= 0; i--) {
      if (((Connection)connections.elementAt (i)).inside (x, y)) {
	return (Connection)connections.elementAt (i);
      }
    }
    return null;
  }

  void _selectAll() {
    Enumeration e = generators.elements();
    while (e.hasMoreElements())
      ((GeneratorBox)e.nextElement()).select();
    e = connections.elements();
    while (e.hasMoreElements())
      ((Connection)e.nextElement()).select();
  }

  void selectAll() {
    int count = countSelected();
    _selectAll();
    if (countSelected() != count)
      repaint();
  }

  void _unselectAll() {
    Enumeration e = generators.elements();
    while (e.hasMoreElements())
      ((GeneratorBox)e.nextElement()).unselect();
    e = connections.elements();
    while (e.hasMoreElements())
      ((Connection)e.nextElement()).unselect();
  }

  void unselectAll() {
    int count = countSelected();
    _unselectAll();
    if (countSelected() != count)
      repaint();
  }

  void selectInside (int x1, int y1, int x2, int y2) {
    int t;
    if (x2 < x1) { t = x1; x1 = x2; x2 = t; }
    if (y2 < y1) { t = y1; y1 = y2; y2 = t; }

    Rectangle r = new Rectangle (x1, y1,
                                 x2 - x1 + 1, y2 - y1 + 1);

    Enumeration genE = generators.elements();
    while (genE.hasMoreElements()) {
      GeneratorBox gen = (GeneratorBox) genE.nextElement();
      if (r.intersects (gen.getRect()))
        gen.select();
    }

    Enumeration conE = connections.elements();
    while (conE.hasMoreElements()) {
      Connection con = (Connection) conE.nextElement();
      if (r.intersects (con.getRect()))
        con.select();
    }

    repaint();
  }

  int countSelectedGenerators() {
    int count = 0;
    Enumeration e = generators.elements();
    while (e.hasMoreElements())
      if (((GeneratorBox) e.nextElement()).isSelected()) count++;
    return count;
  }

  int countSelectedConnections() {
    int count = 0;
    Enumeration e = connections.elements();
    while (e.hasMoreElements())
      if (((Connection) e.nextElement()).isSelected()) count++;
    return count;
  }

  int countSelected() {
    return countSelectedGenerators() + countSelectedConnections();
  }

  void selectOnly (GeneratorBox g) {
    if (countSelected() != 1 || !g.isSelected()) {
      _unselectAll();
      g.select();
      repaint();
    }
  }

  void selectOnly (Connection c) {
    if (countSelected() != 1 || !c.isSelected()) {
      _unselectAll();
      c.select();
      repaint();
    }
  }

  void deleteSelected() {
    int conNo;
    int genNo;
    int inNo;
    Connection con;
    GeneratorBox gen;
    Input in;

    // Remove selected connections
    conNo = 0;
    while (conNo < connections.size()) {
      con = (Connection) connections.elementAt (conNo);
      if (con.isSelected())
        connections.removeElement (con);
      else
        conNo++;
    }

    // Remove selected generators, any connections with them
    genNo = 0;
    while (genNo < generators.size()) {

      gen = (GeneratorBox) generators.elementAt (genNo);
      if (gen.isSelected()) {

        // Check for connections from this generator, and remove them
	conNo = 0;
        while (conNo < connections.size()) {
          con = (Connection) connections.elementAt (conNo);
	  if (gen == con.source)
	    connections.removeElement (con);
	  else
	    conNo++;
        }

        // Check for connections to an input in this generator, and remove them
	conNo = 0;
        while (conNo < connections.size()) {
          con = (Connection) connections.elementAt (conNo);
	  inNo = 0;
	  while (inNo < gen.inputs.size()) {
	    in = (Input) gen.inputs.elementAt (inNo);
	    if (in == con.dest) {
	      connections.removeElement (con);
	      break;
	    }
	    inNo++;
	  }
	  if (inNo == gen.inputs.size())
	    conNo++;
        }

        // finally, remove the generator
	gen.dispose();
        generators.removeElement (gen);

      }
      else
        genNo++;
    }

    repaint();

  }

  void showProperties() {
    if (countSelectedGenerators() == 0) {
      if (propertiesDialog == null)
        propertiesDialog = new SynthProperties (pa, this);
      propertiesDialog.show();
    }
    else {
      Enumeration e = generators.elements();
      GeneratorBox g;
      while (e.hasMoreElements()) {
        if ((g = (GeneratorBox)e.nextElement()).isSelected())
          g.showProperties (pa);
      }
    }
  }

  void save (OutputStream os) throws IOException, SynthIfException {
    DataOutputStream dos = new DataOutputStream (os);
    dos.writeBytes ("ZSyn");	// magic
    dos.writeInt (1);		// file format version

    dos.writeInt (size().width);
    dos.writeInt (size().height);

    dos.writeInt (samplingRate);
    dos.writeDouble (time);

    dos.writeInt (generators.size() + connections.size());

    Enumeration e = generators.elements();
    while (e.hasMoreElements()) {
      GeneratorBox gen = (GeneratorBox) e.nextElement();
      gen.write (dos);
    }

    e = connections.elements();
    while (e.hasMoreElements()) {
      Connection c = (Connection) e.nextElement();
      int sourceNo = -1, destGenNo = -1, destInNo = -1;
      for (int genNo = 0; genNo < generators.size(); genNo++) {
        GeneratorBox gen = (GeneratorBox) generators.elementAt (genNo);
        if (c.source == gen)
	  sourceNo = genNo;
	for (int inNo = 0; inNo < gen.inputs.size(); inNo++) {
	  Input in = (Input) gen.inputs.elementAt (inNo);
	  if (c.dest == in) {
	    destGenNo = genNo;
	    destInNo = inNo;
	  }
	}
      }
      if (sourceNo < 0 || destGenNo < 0 || destInNo < 0)
        throw new SynthIfException ("Invalid internal data structure");
      dos.writeBytes (Connection.IDENT);
      dos.writeInt (sourceNo);
      dos.writeInt (destGenNo);
      dos.writeInt (destInNo);
      dos.writeBoolean (c.isSelected());
    }
  }

  String readAsciiString (DataInputStream in, int n) throws IOException, EOFException {
    byte bytes[] = new byte[n];
    in.readFully (bytes);
    return new String (bytes, 0);
  }

  void load (InputStream in)
                        throws IOException, EOFException, FileFormatException
  {
    DataInputStream din = new DataInputStream (in);
    GeneratorBox gen;
    String s;
    int i;

    try {
    if (!readAsciiString (din, 4).equals ("ZSyn"))
      throw new FileFormatException ("Incorrect magic number");
    if ((i = din.readInt()) != 1)
      throw new FileFormatException ("Unknown version number " + i);
    i = din.readInt();
    prefSize = new Dimension (i, din.readInt());

    samplingRate = din.readInt();
    time = din.readDouble();

    generators = new Vector();
    connections = new Vector();

    int n = din.readInt();

    for (i = 0; i < n; i++) {
      s = readAsciiString (din, 4);

      gen = null;
           if (s.equals (AdderBox.IDENT)) { gen = new AdderBox (this); }
      else if (s.equals (DividerBox.IDENT)) { gen = new DividerBox (this); }
      else if (s.equals (MultiplierBox.IDENT)) { gen = new MultiplierBox(this); }
      else if (s.equals (OscillatorBox.IDENT)) { gen=new OscillatorBox(this); }
      else if (s.equals (OutputBox.IDENT)) { gen = new OutputBox (this); }
      else if (s.equals (ConstantBox.IDENT)) { gen = new ConstantBox (this); }
      else if (s.equals (EnvelopeBox.IDENT)) { gen = new EnvelopeBox (this); }
      else if (s.equals (DelayBox.IDENT)) { gen = new DelayBox (this); }
      else if (s.equals (AbsBox.IDENT)) { gen = new AbsBox (this); }
      else if (s.equals (DistortBox.IDENT)) { gen = new DistortBox (this); }
      else if (s.equals (FilterBox.IDENT)) { gen = new FilterBox (this); }
      else if (s.equals (Connection.IDENT)) {
	int sourceNo = din.readInt();
	int destGenNo = din.readInt();
	int destInNo = din.readInt();
	GeneratorBox source = (GeneratorBox) generators.elementAt (sourceNo);
	Input dest = (Input) ((GeneratorBox)generators.elementAt (destGenNo))
	                                          .inputs.elementAt (destInNo);
        Connection con = new Connection (source, dest);
	if (din.readBoolean()) con.select();

	connections.addElement (con);
      }
      else throw new FileFormatException ("Unknown object: " + s);
      
      if (gen != null) {
        gen.read (din);
	generators.addElement (gen);
      }
    }
    }
    catch (EOFException e) {
      throw new FileFormatException ("Premature End of File");
    }
  }

  public void hideProperties () {
    if (propertiesDialog != null) {
      propertiesDialog.hide();
      propertiesDialog.dispose();
      propertiesDialog = null;
    }
  }

  public boolean action (Event event, Object arg) {
    if (event.target instanceof MenuItem) {
      if (arg.equals ("Synthesize"))
        synthesize();
      else if (arg.equals ("Select all"))
        selectAll();
      else if (arg.equals ("Unselect all"))
        unselectAll();
      else if (arg.equals ("Delete")) {
        deleteSelected();
	repaint();
      }
      else if (arg.equals ("Properties ...")) {
        showProperties();
      }
      else if (arg.equals ("Synthesizer Properties ...")) {
	if (propertiesDialog == null)
          propertiesDialog = new SynthProperties (pa, this);
	propertiesDialog.show();
      }
      else if (arg.equals ("Constant")) {
        newGeneratorGen = new ConstantBox (this);
	newGeneratorFlag = true;
      }
      else if (arg.equals ("Oscillator")) {
        newGeneratorGen = new OscillatorBox (this);
	newGeneratorFlag = true;
      }
      else if (arg.equals ("Adder")) {
        newGeneratorGen = new AdderBox (this);
	newGeneratorFlag = true;
      }
      else if (arg.equals ("Multiplier")) {
        newGeneratorGen = new MultiplierBox (this);
	newGeneratorFlag = true;
      }
      else if (arg.equals ("Divider")) {
        newGeneratorGen = new DividerBox (this);
	newGeneratorFlag = true;
      }
      else if (arg.equals ("Envelope")) {
        newGeneratorGen = new EnvelopeBox (this);
	newGeneratorFlag = true;
      }
      else if (arg.equals ("Delay")) {
        newGeneratorGen = new DelayBox (this);
	newGeneratorFlag = true;
      }
      else if (arg.equals ("Filter")) {
        newGeneratorGen = new FilterBox (this);
	newGeneratorFlag = true;
      }
      else if (arg.equals ("Absolute Val.")) {
        newGeneratorGen = new AbsBox (this);
	newGeneratorFlag = true;
      }
      else if (arg.equals ("Distort")) {
        newGeneratorGen = new DistortBox (this);
	newGeneratorFlag = true;
      }
      else if (arg.equals ("Output")) {
        newGeneratorGen = new OutputBox (this);
	newGeneratorFlag = true;
      }
      else
	return false;

      if (newGeneratorFlag) {
        pa.setCursor (Frame.CROSSHAIR_CURSOR);
        pa.statusBar.setText ("Select location for new generator");
      }
    }
    else if (event.target == pa.deleteButton) {
      deleteSelected();
      repaint();
    }
    else if (event.target == pa.propertiesButton)
      showProperties();
    else if (event.target == pa.synthesizeButton)
      synthesize();

    else return false;
    return true;
  }

  public boolean mouseDown (Event event, int x, int y) {
    dragStart = new Point (x, y);
    dragged = false;

    if (newGeneratorFlag) { 
      if ((event.modifiers & Event.META_MASK) == 0) {
        newGeneratorGen.move (x, y);
        generators.addElement (newGeneratorGen);
        selectOnly (newGeneratorGen);
        newGeneratorGen.showProperties (pa);
      }
      newGeneratorFlag = false;
      newGeneratorGen = null;
      pa.setCursor (Frame.DEFAULT_CURSOR);
      pa.statusBar.clearText();
    }
    else if (moveOriginFlag) { }
    else if (selectBoxFlag) { }
    else if (moveGeneratorFlag) { }
    else if ((event.modifiers & Event.META_MASK) != 0)
    {
      rubbers = new RubberBand[1];
      rubbers[0] = new LineRubberBand (x, y);
      moveOriginFlag = true;
    }
    else {
      Connection con;
      GeneratorBox gen;
      if ((con = inConnection (x, y)) != null) {
        if ((event.modifiers & Event.CTRL_MASK) != 0 ||
            (event.modifiers & Event.ALT_MASK) != 0) {
          if (con.isSelected()) con.unselect();
          else con.select();
          repaint();
        }
        else 
          selectOnly (con);
      }
      else if ((gen = inGenerator (x, y)) != null) {
        if ((event.modifiers & Event.CTRL_MASK) != 0 ||
            (event.modifiers & Event.ALT_MASK) != 0) {
          if (gen.isSelected()) gen.unselect();
          else gen.select();
          repaint();
        }
        else if (event.clickCount == 2) {
          selectOnly (gen);
          showProperties();
        }
        else {
          Input in;
          if (gen.inBorder (x, y)) {
            if (!gen.isSelected()) {
	      _unselectAll();
	      gen.select();
	      moveGeneratorSelectedFlag = true;
	    }
	    else moveGeneratorSelectedFlag = false;
            rubbers = new RubberBand [countSelectedGenerators()];
            moveGeneratorGens = new GeneratorBox [countSelectedGenerators()];
            Enumeration genE = generators.elements();
            int i = 0;
            while (genE.hasMoreElements()) {
              GeneratorBox g = (GeneratorBox) genE.nextElement();
	      if (g.isSelected()) {
                rubbers[i] = new BoxRubberBand (g.getSize(),
	                                        x-g.getPosition().x,
				                y-g.getPosition().y);
                moveGeneratorGens[i] = g;
	        i++;
              }
            }
            moveGeneratorFlag = true;
            moveGeneratorGen = gen;
            return true;
          }
          else {
            rubbers = new RubberBand[1];
            rubbers[0] = new LineRubberBand (gen.getCenter());
            selectDestFlag = true;
            selectDestSource = gen;
            return true;
          }
        }
      }
      else {
        if (event.clickCount == 2) {
          unselectAll();
          showProperties();
        } else {
          rubbers = new RubberBand[1];
          rubbers[0] = new BoxStretchRubberBand (x, y);
          selectBoxAddFlag = (event.modifiers & Event.CTRL_MASK) != 0 ||
                             (event.modifiers & Event.ALT_MASK) != 0;
          selectBoxFlag = true;
        }
      }
    }
    return super.mouseDown (event, x, y);
  }
  
  public boolean mouseMove (Event event, int x, int y) {
    if (newGeneratorFlag) { }
    else if (moveOriginFlag) { }
    else if (selectBoxFlag) { }
    else if (moveGeneratorFlag) { }
    else if (selectDestFlag) { }
    else { }
    return super.mouseMove (event, x, y);
  }

  public boolean mouseDrag (Event event, int x, int y) {
    boolean firstDrag = false;

    // only drag if mouse has moved a certain number of pixels
    // (mainly to work around another win32 awt bug)
    if (!dragged &&
        (x < dragStart.x-dragThreshold || x > dragStart.x+dragThreshold ||
	y < dragStart.y-dragThreshold || y > dragStart.y+dragThreshold))
    {
      dragged = true;
      firstDrag = true;
    }
    if (!dragged)
      return super.mouseDrag (event, x, y);

    if (newGeneratorFlag) { }
    else if (moveOriginFlag) { 
      if (firstDrag) 
        pa.setCursor (Frame.HAND_CURSOR);
    }
    else if (moveGeneratorFlag) { 
      if (firstDrag)
        pa.setCursor (Frame.MOVE_CURSOR);
    }
    else if (selectDestFlag) { 
      if (firstDrag) 
        pa.statusBar.setText ("Select destination for connection");
    }

    if (rubbers != null) {
      Graphics g = getGraphics();
      for (int i = 0; i < rubbers.length; i++)
        rubbers[i].move (g, x, y);
      g.dispose();
      return true;
    }
    else return super.mouseDrag (event, x, y);
  }

  public boolean mouseUp (Event event, int x, int y) {

    RubberBand rubbers[] = this.rubbers;

    if (rubbers != null) {
      Graphics g = getGraphics();
      for (int i = 0; i < rubbers.length; i++)
        rubbers[i].erase (g);
      g.dispose();
      this.rubbers = null;
    }

    if (newGeneratorFlag) { }
    else if (moveOriginFlag) {
      if (dragged) {
        Point p = rubbers[0].getStart();
        for (int i = 0; i < generators.size(); i++) {
	  ((GeneratorBox)generators.elementAt(i)).translate (x-p.x, y-p.y);
	}
        pa.setCursor (Frame.DEFAULT_CURSOR);
        repaint();

      }

      moveOriginFlag = false;
      return true;
    }
    else if (selectBoxFlag) {
      if (dragged) {
	if (!selectBoxAddFlag) _unselectAll();
	selectInside (rubbers[0].getStart().x, rubbers[0].getStart().y, x, y);
      }
      else if (!selectBoxAddFlag)
        unselectAll();
      selectBoxFlag = false;
    }
    else if (moveGeneratorFlag) {
      if (dragged) {
        for (int i = 0; i < moveGeneratorGens.length; i++) {
          Point p = ((BoxRubberBand)rubbers[i]).getOffset();
          moveGeneratorGens[i].move (x - p.x, y - p.y);
	}
        pa.setCursor (Frame.DEFAULT_CURSOR);
        repaint();
      }
      else {
        if (moveGeneratorSelectedFlag)
	  repaint();
	else
          selectOnly (moveGeneratorGen);
      }
      moveGeneratorGen = null;
      moveGeneratorGens = null;
      moveGeneratorFlag = false;
      return true;
    }
    else if (selectDestFlag) {
      if (dragged) {
        GeneratorBox gen = inGenerator (x, y);
        Input in;
        if (gen != null && gen != selectDestSource &&

	        (in = gen.inInput (x, y)) != null) 
        {
	  boolean allowConnection = true;
	  if (!in.allowMultipleConnections) {
	    Enumeration e = connections.elements();
	    boolean alreadyConnected = false;
	    while (e.hasMoreElements())
	      if (((Connection)e.nextElement()).dest == in)
	        allowConnection = false;
          }
	  if (!allowConnection)
	    pa.statusBar.setEmphasizedText ("This input already has a connection.", 5000);
	  else {
            Connection c = new Connection (selectDestSource, in);
            connections.addElement (c);
            selectOnly (c);
	    pa.statusBar.clearText();
	  }
        }
        else pa.statusBar.setEmphasizedText ("Bad destination.", 5000);
      }
      else
        selectOnly (selectDestSource);
      selectDestFlag = false;
      selectDestSource = null;
      return true;
    }

    return super.mouseUp (event, x, y);
  }

  public void paint (Graphics g) {
    for (int i = 0; i < generators.size(); i++) {
      ((GeneratorBox)generators.elementAt (i)).draw (g);
    }
    for (int i = 0; i < connections.size(); i++) {
      ((Connection)connections.elementAt (i)).draw (g);
    }
  }

  public void update (Graphics g) {
    g.setPaintMode();	// netscape mis-feature workaround
    super.update (g);
  }
}
