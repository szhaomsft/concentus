/* Copyright (c) 2007-2008 CSIRO
   Copyright (c) 2007-2011 Xiph.Org Foundation
   Originally written by Jean-Marc Valin, Gregory Maxwell, Koen Vos,
   Timothy B. Terriberry, and the Opus open-source contributors
   Ported to Java by Logan Stromberg

   Redistribution and use in source and binary forms, with or without
   modification, are permitted provided that the following conditions
   are met:

   - Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

   - Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.

   - Neither the name of Internet Society, IETF or IETF Trust, nor the
   names of specific contributors, may be used to endorse or promote
   products derived from this software without specific prior written
   permission.

   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
   ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
   LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
   A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
   OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
   EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
   PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
   LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
   NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
   SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

using Concentus.Celt.Structs;
using Concentus.Common;
using Concentus.Common.CPlusPlus;
using Concentus;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

package org.concentus;
{
    class TonalityAnalysisState
    {
        internal boolean enabled = false;
        internal final float[] angle = new float[240];
        internal final float[] d_angle = new float[240];
        internal final float[] d2_angle = new float[240];
        internal final int[] inmem = new int[OpusConstants.ANALYSIS_BUF_SIZE];
        internal int mem_fill;                      /* number of usable samples in the buffer */
        internal final float[] prev_band_tonality = new float[OpusConstants.NB_TBANDS];
        internal float prev_tonality;
        internal final float[][] E = Arrays.InitTwoDimensionalArray<float>(OpusConstants.NB_FRAMES, OpusConstants.NB_TBANDS);
        internal final float[] lowE = new float[OpusConstants.NB_TBANDS];
        internal final float[] highE = new float[OpusConstants.NB_TBANDS];
        internal final float[] meanE = new float[OpusConstants.NB_TOT_BANDS];
        internal final float[] mem = new float[32];
        internal final float[] cmean = new float[8];
        internal final float[] std = new float[9];
        internal float music_prob;
        internal float Etracker;
        internal float lowECount;
        internal int E_count;
        internal int last_music;
        internal int last_transition;
        internal int count;
        internal final float[] subframe_mem = new float[3];
        internal int analysis_offset;
        /** Probability of having speech for time i to DETECT_SIZE-1 (and music before).
            pspeech[0] is the probability that all frames in the window are speech. */
        internal final float[] pspeech = new float[OpusConstants.DETECT_SIZE];
        /** Probability of having music for time i to DETECT_SIZE-1 (and speech before).
            pmusic[0] is the probability that all frames in the window are music. */
        internal final float[] pmusic = new float[OpusConstants.DETECT_SIZE];
        internal float speech_confidence;
        internal float music_confidence;
        internal int speech_confidence_count;
        internal int music_confidence_count;
        internal int write_pos;
        internal int read_pos;
        internal int read_subframe;
        internal final AnalysisInfo[] info = new AnalysisInfo[OpusConstants.DETECT_SIZE];

        internal TonalityAnalysisState()
        {
            for (int c = 0; c < OpusConstants.DETECT_SIZE; c++)
            {
                info[c] = new AnalysisInfo();
            }
        }

        internal void Reset()
        {
            Arrays.MemSet<float>(angle,0, 240);
            Arrays.MemSet<float>(d_angle,0, 240);
            Arrays.MemSet<float>(d2_angle,0, 240);
            Arrays.MemSet(inmem, 0, OpusConstants.ANALYSIS_BUF_SIZE);
            mem_fill = 0;
            Arrays.MemSet<float>(prev_band_tonality,0, OpusConstants.NB_TBANDS);
            prev_tonality = 0;
            for (int c = 0; c < OpusConstants.NB_FRAMES; c++)
            {
                Arrays.MemSet<float>(E[c], 0, OpusConstants.NB_TBANDS);
            }
            Arrays.MemSet<float>(lowE,0, OpusConstants.NB_TBANDS);
            Arrays.MemSet<float>(highE,0, OpusConstants.NB_TBANDS);
            Arrays.MemSet<float>(meanE,0, OpusConstants.NB_TOT_BANDS);
            Arrays.MemSet<float>(mem,0, 32);
            Arrays.MemSet<float>(cmean,0, 8);
            Arrays.MemSet<float>(std,0, 9);
            music_prob = 0;
            Etracker = 0;
            lowECount = 0;
            E_count = 0;
            last_music = 0;
            last_transition = 0;
            count = 0;
            Arrays.MemSet<float>(subframe_mem,0, 3);
            analysis_offset = 0;
            Arrays.MemSet<float>(pspeech,0, OpusConstants.DETECT_SIZE);
            Arrays.MemSet<float>(pmusic,0, OpusConstants.DETECT_SIZE);
            speech_confidence = 0;
            music_confidence = 0;
            speech_confidence_count = 0;
            music_confidence_count = 0;
            write_pos = 0;
            read_pos = 0;
            read_subframe = 0;
            for (int c = 0; c < OpusConstants.DETECT_SIZE; c++)
            {
                info[c].Reset();
            }
        }
    }
}