import { Refresh } from "@suid/icons-material";
import { Button } from "@suid/material";
import { createSignal, useContext } from "solid-js";
import { I18nContext } from "../provider/I18nProvider";
import { ProjectContext } from "../provider/ProjectProvider";

export const RefetchButton = () => {
    const i18n = useContext(I18nContext);
    const pCtx = useContext(ProjectContext);
    const [refetching, setRefetching] = createSignal(false);

    const handleRefetch = async () => {
        if (!pCtx?.refetchAll || refetching()) return;

        setRefetching(true);
        try {
            await pCtx.refetchAll();
        } catch (error) {
            console.error("Failed to refetch data", error);
        } finally {
            setRefetching(false);
        }
    };

    return (
        <Button
            variant="outlined"
            startIcon={<Refresh />}
            onClick={() => { void handleRefetch(); }}
            disabled={refetching()}
        >
            {i18n?.t("common.refresh")}
        </Button>
    );
};
