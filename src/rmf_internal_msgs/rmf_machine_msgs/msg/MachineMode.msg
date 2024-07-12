# The DoorMode message captures the "mode" of an automatic door controller.
# Most door controllers default to running in "closed" mode, and transition
# through some sort of "moving" mode until reaching the "open" mode.

uint32 value

# "value" must be one of the following enumerations:
uint32 MODE_IDLE=0
uint32 MODE_WORKING=1
uint32 MODE_OFFLINE=2
uint32 MODE_UNKNOWN=3
