# generated by datamodel-codegen:
#   filename:  event_description_GoToPlace.json

from __future__ import annotations

from pydantic import BaseModel, Field

from . import Place


class GoToPlaceEventDescription(BaseModel):
    __root__: Place.PlaceDescription = Field(
        ...,
        description="Have a robot go to a place",
        title="Go To Place Event Description",
    )
