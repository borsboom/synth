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

package zrs.wave;

import sun.audio.*;

public class SingleAudioPlayer {
  AudioDataStream audioDataStream = null;

  public SingleAudioPlayer() {}

  public void play (Wave wave) {
    stop();
    if (wave != null) {
      byte[] ulaw = wave.getUlaw();
      if (ulaw != null)
        play (ulaw);
    }
  }

  public void play (byte[] ulaw) {
    stop();
    AudioData audioData = new AudioData (ulaw);
    audioDataStream = new AudioDataStream (audioData);
    AudioPlayer.player.start (audioDataStream);
  }

  public void loop (Wave wave) {
    stop();
    if (wave != null) {
      byte[] ulaw = wave.getUlaw();
      if (ulaw != null)
        loop (ulaw);
    }
  }

  public void loop (byte[] ulaw) {
    stop();
    AudioData audioData = new AudioData (ulaw);
    audioDataStream = new ContinuousAudioDataStream (audioData);
    AudioPlayer.player.start (audioDataStream);
  }

  public void stop() {
    if (audioDataStream != null) {
      AudioPlayer.player.stop (audioDataStream);
      audioDataStream = null;
    }
  }

  public boolean isPlaying() {
    return audioDataStream != null;
  }

  public boolean isLooping() {
    return audioDataStream != null &&
           audioDataStream instanceof ContinuousAudioDataStream;
  }

}
