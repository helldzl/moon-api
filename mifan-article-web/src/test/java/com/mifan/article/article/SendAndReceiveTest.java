package com.mifan.article.article;

import com.mifan.article.AbstractTests;
import com.mifan.article.domain.TopicsClassification;
import com.mifan.article.domain.TopicsClustering;
import org.junit.Test;
import org.moonframework.amqp.Data;
import org.moonframework.amqp.Resource;
import org.moonframework.core.amqp.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by LiuKai on 2017/7/7.
 */
public class SendAndReceiveTest extends AbstractTests {
    @Autowired
    private Message message;
    @Autowired
    private RabbitTemplate template;

    //过滤emoji表情
    @Test
    public void testMessage() {
        Map<String, String> meta = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        Map<String, Set<Long>> relationships = new HashMap<>();
        meta.put("name", "test");
        meta.put("origin", "http://test.shiwuTest11.com");
        meta.put("className", "com.mifan.article.domain.TopicsDocument");

        data.put("forumId", 3);
        data.put("title", "shiwuTest11");
        data.put("author", "lkshiwuTest");
        data.put("brand", "dddshiwuTest");
        data.put("description", "descriptionshiwuTest");
        data.put("content", "<img src=\"http://static.budee.com/yyren/image/205/29/1953106.jpg\" alt=\"RoRo Diamante looking at the camera\" width=\"760\" height=\"350\"> \n" +
                "<strong><em>RoRo (Rochelle) Diamante is a leading light on YouTube who has penned and produced her own solo album.</em></strong> \n" +
                "<em>She explains how she learned to listen to heart and her voice and ended up with studio magic.</em> \n" +
                "<strong>What advice would you have for emerging artists who want to make a record?</strong>\n" +
                "<br> Just do it! We can always make excuses or put a bunch of obstacles in our way that make us think we can’t do that one thing we want to do. I created this album from scratch all by myself with no knowledge of production whatsoever! I knew how to write music but I didn’t know how to produce. But it was what I needed to do. \n" +
                "<strong>Describe the process behind your debut album? </strong>\n" +
                "<br> It took me about 10 months to create “Release”. Just me, myself and I in my little home studio! This album came about at a time when I was fresh out of a management contract and record deal so I wanted some time to be by myself and listen to my heart. There were just magical moments when I knew that a song was “the one” for the album and I just followed those moments. \n" +
                "<strong>How was your experience recording and listening back to your voice?</strong>\n" +
                "<br> Just myself in my home studio… I was WAY more critical of my vocals than when I’m collaborating with another producer. I’ve been in recording booths since I was 10 years old so it’s something that’s always been exciting for me. \n" +
                "<strong>How challenging is it to replicate studio vibes live on stage?</strong>\n" +
                "<br> It’s two completely different vibes. For me personally, I’m very relaxed and comfy in a recording booth. But on stage, I’m excited and in this performance zone. It’s like I’ve entered another dimension. I love them both. \n" +
                "<strong>What role does social media play in your career?</strong>\n" +
                "<br> Social media not only lets people know when I’m doing something new but it lets them come along for the journey so that they were a part of it as well. \n" +
                "<strong>How important is branding for recording artists?</strong>\n" +
                "<br> People need to know who you are and what you’re all about so they know what they’re getting into. When it’s clear who you are, you can create super fans who are dedicated to your brand. \n" +
                "<strong>How about videos?</strong>\n" +
                "<br> Videos make it easy to spread the word about upcoming things in your career. And you have opportunities to attract new fans through your video’s content. For example, when I do covers of popular music, I get new fans that were simply searching for that specific song. Now I can inform them about my own original music as well.  \n" +
                "<blockquote cite=\"https://www.facebook.com/roroofficial7/videos/1380545541987341/\"> \n" +
                " <p><a href=\"https://www.facebook.com/roroofficial7/videos/1380545541987341/\"></a></p> \n" +
                " <p>This is a fun video for you guys to enjoy and hear one of my FAV songs \"1,2,3\" off of my new album \"Release\"! Go check out the album! You'll love it because it was made with love…and sparkles…and unicorns\uD83D\uDE03 (Made with Vidial Video App) #TCHelicon #truetoyourself #roroofficial #ReleaseAlbum #AvailableNow #songwriter #producer #popmusic</p> \n" +
                " <p>Opslået af <a href=\"https://www.facebook.com/roroofficial7/\">RoRo</a> på 18. januar 2017</p> \n" +
                "</blockquote> \n" +
                "<em>This video was created using the new TC-Helicon app ‘VIDIAL’</em> \n" +
                "<strong>What big lessons have you learned about the recording process so far?</strong>\n" +
                "<br> I have learned that it’s really personal. Being in that vocal booth and closing your eyes and pouring your heart out on a microphone is a one in a million feeling. \n" +
                "<strong>What have you found is the best platform for selling music?</strong>\n" +
                "<br> iTunes seems to be one of the most used platforms for buying new music. Every time I have a new release, I’m always asked by my fans if it will be on iTunes. For hard copies, Amazon is really simple to go through. \n" +
                "<strong>Any plans to perform the album live?</strong>\n" +
                "<br> This coming march, I am excited to say that I will be flying down to Orlando, FL to perform at a charity event where I will be performing songs such as “Angel in Blue” and “Queen Bee” to name a few. I am booking a lot more gigs to take the album out for a spin. \n" +
                "<em>Create your own professional looking videos with the brand new app, Vidial. Find out more here:&nbsp;<a href=\"http://www.vidial.com/\">www.vidial.com</a></em> \n" +
                "<img src=\"http://static.budee.com/yyren/image/205/29/1953107.jpg\" alt=\"\" width=\"150\" height=\"150\">\n" +
                "<em><strong>RoRo</strong>&nbsp;(Rochelle Diamante) is a singer/songwriter who has accomplished an impressive amount for her age. She regularly posts videos on her YouTube Channel. Her debut album ‘Release’ is out now.</em> \n" +
                "<a href=\"http://www.youtube.com/roroofficial7\">YouTube</a>&nbsp;|&nbsp;\n" +
                "<a href=\"https://www.facebook.com/roroofficial7/\">Facebook</a>&nbsp;|&nbsp;\n" +
                "<a href=\"https://twitter.com/roroofficial7\">Twitter</a>");
        data.put("parentId", 0l);
        data.put("seedId", 2l);
        data.put("origin", meta.get("origin"));
        data.put("originHash", 111L);
        //data.put("parentId",0L);
        data.put("creator", 0l);

        Set<Long> longSet = new HashSet<>();
        longSet.add(999999999L);
        longSet.add(1111111111L);
        longSet.add(111122L);
        longSet.add(111122222L);
        relationships.put("images", longSet);
        message.sendAndReceive(meta, data, relationships);


    }

    @Test
    public void testMessageAttachment() {
        Map<String, String> meta = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        meta.put("name", "images");
        System.out.println("1111");
        meta.put("origin", "http://img0.imgtn.bdimg.com/it/u=2607522353,2439730680&fm=214&gp=0.jpg");
        meta.put("className", "com.mifan.article.domain.Attachments");

        data.put("origin", "http://img0.imgtn.bdimg.com/it/u=2607522353,2439730680&fm=214&gp=0.jpg");
        data.put("title", "test007");
        data.put("enabled", 0);
        data.put("mime", "image/jpeg");
        data.put("filename", "http://ddddd.com");
        message.sendAndReceive(meta, data, null);
    }

    //发送一个消息给线上，进行分类使用
    @Test
    public void sendMessage() {
        String exchange = "server.data.direct.exchange3";
        String routingKey = "server.data.direct.queue3";
        String id = "1";
        String type = "com.mifan.article.domain.Topics";
        Data data = new Data(id, type);
        Map<String, Object> map = new HashMap<>();
        map.put("moderated", 0);
        map.put("docId", 1);
        map.put("modifier", 0);
        map.put("title", "Virus TI2 Desktop");
        map.put("enabled", 1);
        map.put("imageSingle", false);
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("creator", 0);
        postMap.put("postType", "CRAWLER");
        postMap.put("created", new Date());
        postMap.put("modifier", 0);
        postMap.put("description", "Analog Modeling Desktop Synthesizer and 24-bit/192kHz Audio/MIDI Interface");
        postMap.put("postTypeValue", "爬取");
        postMap.put("priority", 0);
        postMap.put("title", "Virus TI2 Desktop");
        postMap.put("content", "<div> \n" +
                " <h2>Access Steps Up The Renowned Virus!</h2> \n" +
                " <p>The Access Virus TI2 Desktop synthesizer takes the revered Virus TI to the next level, boasting 25% more calculating power, a more robust onboard effects section, a lighter weight, and a completely redesigned housing. At the heart of the Virus TI2 Desktop is the new OS3 which comes with new effects - Frequency Shifter, Tape Delay, new Distortions, and Character. What's more, it now includes the enhanced Virus Control 5 plug-in, giving you even deeper control of the synthesizer and your many presets. Already a known Ferrari of a synth, expect even more from the Access Virus TI2 Desktop.</p> \n" +
                " <p></p> \n" +
                " <p></p> \n" +
                " <strong>Access Virus TI2 Desktop Synthesizer at a Glance:</strong> \n" +
                " <ul> \n" +
                "  <li>The \"Ferrari\" Just Got Better </li> \n" +
                "  <li>Robust effects section, with new TI2 effects </li> \n" +
                "  <li>Total Integration - enhanced Virus Control 3.0 plug-in </li> \n" +
                "  <li>3 main oscillators and one sub-oscillator per voice </li> \n" +
                "  <li>Two fully independent filters and 2-dimensional modulation matrix </li> \n" +
                "  <li>Robust effects section, with new TI2 effects </li> \n" +
                "  <li>Each patch contains its own arpeggiator pattern </li> \n" +
                "  <li>Premium redesigned enclosure, lighter in weight</li> \n" +
                " </ul> \n" +
                " <strong>The \"Ferrari\" Just Got Better</strong> \n" +
                " <p> Access somehow managed to improve on the revered Virus TI system, taking the \"Ferrari\" to the next level of performance - and TI computer integration. With a lighter, redesigned enclosure, a bolstered effects section, and an enhanced Virus Control 3.0 plug-in - plus all the staples that made the Virus the famous powerhouse - the Virus TI2 Desktop is truly the culmination of Access's 12-year triathlon of sound research, distillation of user input, and their simple desire to create an exceptional instrument.</p> \n" +
                " <strong>Robust effects section, with new TI2 effects</strong> \n" +
                " <p> The new Access Virus TI2 Desktop features an even more powerful effects section, bringing studio-favorite effects to the live performance realm. In addition to phaser, chorus/flanger, ring modulator/shifter, EQ, and a global vocoder, onboard are a new Tape Delay effect, a Frequency Shifter, and new Distortions. There's also a new Character effect, which lets you shape the timbre of the Virus's sound and its fit in the mix, using Analog Boost, Vintage 1/2/3, Pad Opener, Lead Enhancer, Bass Enhancer, or Stereo Widener \"Characters.\"</p> \n" +
                " <strong>Total Integration - enhanced Virus Control 5 plug-in</strong> \n" +
                " <p> The TI in the Virus TI2 Desktop stands for Total Integration with your DAW - and Access has heightened that integration, with the Access Virus TI2 line. With the new Virus Control 5 plug-in, you have enhanced control over your Virus TI2, right inside your DAW. There's also an even more robust presets manager, so you can organize, sort, search, and tweak - as quickly as you want to work.</p> \n" +
                " <strong>3 main oscillators and one suboscillator per voice</strong> \n" +
                " <p> The Access Virus TI2 Desktop features three main oscillators and one sub-oscillator per voice. Each main oscillator can be made up of various oscillator types, including Hyper Saw (a multi saw-tooth oscillator with up to 9 stacked oscillators, 9 sub oscillators, and a sync oscillator at the same time), Classic Virtual Analog oscillators (saw, variable pulse, sine, triangle, 62 spectral waves with several FM modes), Graintable, Wavetable (with 100 multi-index wavetables), and Format oscillators</p> \n" +
                " <strong>Two fully independent filters and 2-dimensional modulation matrix</strong> \n" +
                " <p> You can use two fully independent filters with the Access Virus TI2 Desktop - lowpass, highpass, bandpass, and bandstop) while using an optional saturation module between both filter blocks. The saturation module can add one of several distortions and lo-fi effects, or an additional low/highpass filter. There's also optional self-resonating Moog cascade filter simulation, with circuit overload and 1-4 poles. What's more, the Access Virus TI2 Desktop offers a 2-dimensional modulation matrix, with 6 slots (1 source and 3 modulation targets each), and you can modulate parameters in real-time. </p> \n" +
                " <strong>Each patch contains its own arpeggiator pattern</strong> \n" +
                " <p> Every patch onboard the Access Virus TI2 keyboard features its own arpeggiator pattern with 32 programmable steps (length and velocity can be adjusted per step) and a global control for swing/shuffle timing and for note lengths. You can control all of this using the modulation matrix.</p> \n" +
                " <strong>Premium redesigned enclosure, lighter in weight</strong> \n" +
                " <p> The new Access Virus TI2 Desktop retains is classy look - all designed, engineered, and built in Germany. It also features a lighter weight, so the Virus TI2 Desktop is even more prepared for the stage.</p> \n" +
                " <strong>Access Virus TI2 Desktop Synthesizer Features:</strong> \n" +
                " <ul> \n" +
                "  <li>New Effects, including Tape Delay, Frequency Shifter, new Distortions, and Character </li> \n" +
                "  <li>25% more calculating power than Virus TI, and completely redesigned housing </li> \n" +
                "  <li>Dual DSP system with over 80 stereo voices under average load. (Load depends on which oscillator / filter model has been chosen). </li> \n" +
                "  <li>Virus Control 5 VST and Apple Audio Unit Plug-for Mac OS X and Windows. The remote seamlessly integrates the Virus TI into your sequencer, making it feel just like a plug-in. </li> \n" +
                "  <li>The Virus TI's Audio and MIDI inputs and outputs can be used by the sequencer application as an audio and MIDI interface. </li> \n" +
                "  <li>The Virus TI is the first hardware synthesizer with sample-accurate timing and delay-compensated connection to your sequencer. </li> \n" +
                "  <li>WaveTable Oscillators for a completely new array of sounds. WaveTable and conventional Virus oscillators and filters can be mixed. </li> \n" +
                "  <li>HyperSaw oscillators with up to 9 sawtooths - each with parallel sub oscillator per voice (that's over 1800 stereo oscillators @ 100 voices!). </li> \n" +
                "  <li>Independent delay and reverb for all 16 multi mode slots. </li> \n" +
                "  <li>129 parallel effects. There is reverb and delay, chorus, phaser, ring modulator, distortion, 3 band EQ and the Analog Boost bass enhancer. </li> \n" +
                "  <li>2 multi-mode filters (HP, LP, BP, BS) and the Analog Filter (modeled after the MiniMoog cascade filter with 6-24dB Slope and self-oscillation). </li> \n" +
                "  <li>Dedicated remote mode turns the Virus TI into an universal remote control for VST / AU plug-ins and external synthesizers. </li> \n" +
                "  <li>6 balanced outputs with +4dB level and switchable soft limiting algorithm. Studio grade 192kHz D/A converters with S/PDIF digital I/O. 2x24 bit inputs. Surround sound capabilities </li> \n" +
                "  <li>Tap tempo button. The algorithm is based on Access' Sync Xtreme technology. </li> \n" +
                "  <li>Programmable arpeggiator pattern for every patch. </li> \n" +
                "  <li>Knob quantize for creating stepped controller movements. The stepping automatically syncs to the Virus clock or an incoming MIDI clock. </li> \n" +
                "  <li>3 LFOs with 68 waveforms to choose from. </li> \n" +
                "  <li>2 super fast ADSTR envelopes. </li> \n" +
                "  <li>Extended memory: 512 RAM patches and 2048 ROM patches (rewritable). </li> \n" +
                "  <li>Adaptive control smoothing for jitter-free modulations on all important parameters. </li> \n" +
                "  <li>Multi mode with embedded patches. </li> \n" +
                "  <li>Compatible with USB 2.0 specifications, USB and High-Speed USB devices. </li> \n" +
                "  <li>2 pedal inputs.</li> \n" +
                " </ul> \n" +
                " <span>The Access Virus TI2 Desktop takes the renowned Virus TI synthesizer to the next level!</span> \n" +
                "</div>");
        postMap.put("enabled", 1);
        postMap.put("parentId", 0);
        Map<Integer, Object> tagsMap = new HashMap<>();
        tagsMap.put(0, "VirusTI2Desk");
        postMap.put("tags", tagsMap);
        List featuresList = new ArrayList<>();
        Map<String, String> featuresList1 = new HashMap<>();
        featuresList1.put("_name", "Sound Engine Type(s)");
        featuresList1.put("_value", "Analog Modeling");
        featuresList.add(featuresList1);
        postMap.put("features", featuresList);
        postMap.put("topicId", 1L);
        postMap.put("modified", new Date());
        postMap.put("id", 1L);
        Map<Integer, Object> categoriesMap = new HashMap<>();
        categoriesMap.put(0, "Keyboards & MIDI");
        categoriesMap.put(1, "Synths / Modules");
        postMap.put("categories", categoriesMap);

        map.put("post", postMap);
        map.put("reviews", 0);
        map.put("userLike", false);
        map.put("modified", new Date());
        Map<String, Object> fromMap = new HashMap<>();
        fromMap.put("topicId", 1L);
        fromMap.put("reviews", 0);
        fromMap.put("origin", "http://www.sweetwater.com/store/detail/VirusTI2Desk");
        fromMap.put("seedId", 2L);
        fromMap.put("rating", 0.9);
        fromMap.put("id", 1L);
        fromMap.put("originHash", -6949920113737340651L);
        map.put("from", fromMap);
        map.put("id", 1L);
        map.put("locked", 0);
        map.put("creator", 0);
        data.setAttributes(map);

        Map<String, Object> meta = new HashMap<>();
        meta.put(Resource.META_DATETIME, LocalDateTime.now());
        meta.put("target_classification", TopicsClassification.class.getName());
        meta.put("method", Resource.Method.PATCH);
        meta.put("target_cluster", TopicsClustering.class.getName());

        template.convertAndSend(exchange, routingKey, new Resource(meta, data), new CorrelationData(id));

    }


}
