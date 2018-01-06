package workshop.simStreamJoinFork

import spinal.core._
import spinal.core.sim._
import spinal.lib._

object SimStreamUtils {
  //Fork a thread to constantly randomize the valid/payload signals of the given stream
  def streamMasterRandomizer(stream : Stream[UInt], clockDomain: ClockDomain): Unit = fork{
    stream.valid #= false
    while(true){
      stream.valid.randomize()
      stream.payload.randomize()
      clockDomain.waitSampling()
    }
  }

  //Fork a thread to constantly randomize the ready signal of the given stream
  def streamSlaveRandomizer(stream : Stream[UInt], clockDomain: ClockDomain): Unit = fork{
    while(true){
      stream.ready.randomize()
      clockDomain.waitSampling()
    }
  }

  //Fork a thread which will call the body function each time a transaction is consumed on the given stream
  def onStreamFire(stream : Stream[UInt], clockDomain: ClockDomain)(body : => Unit@suspendable): Unit = fork{
    while(true) {
      clockDomain.waitSampling()
      var dummy = if (stream.valid.toBoolean && stream.ready.toBoolean) {
        body
      }
    }
  }
}