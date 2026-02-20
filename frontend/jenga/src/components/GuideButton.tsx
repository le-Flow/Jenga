import { Button } from "@suid/material"
import { useContext } from "solid-js";
import { GuideContext } from "../provider/GuideProvider"

export const GuideButton = () => {

    const gCtx = useContext(GuideContext);

    return (
        <Button 
            onClick={() => gCtx?.startGuide()}
            variant="contained"
        >
            Guide
        </Button>
    )
}