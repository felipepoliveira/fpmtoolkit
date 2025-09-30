import { LoadingOutlined } from "@ant-design/icons";
import { Avatar, List } from "antd";
import { ItemType } from "antd/es/breadcrumb/Breadcrumb";
import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router";
import { usePaginatedQuery } from "../../../../api/backend-api/hooks";
import { ProjectService } from "../../../../api/backend-api/project";
import { ProjectModel } from "../../../../types/backend-api/project";
import NavigationBar from "../../../@components/NavigationBar/NavigationBar";
import ReadContainer from "../../../@components/ReadContainer/ReadContainer";
import { SelectedOrganization, useSelectedOrganizationProvider } from "../hooks";

const ITEMS_PER_PAGE = 20

export default function OrganizationProjectsListPage() {

    const selectedOrganizationProvider = useSelectedOrganizationProvider()
    const [selectedOrganization, setSelectedOrganization] = useState<SelectedOrganization>()
    const projectsQuery = usePaginatedQuery<ProjectModel>({
        dataSourceFn: async (currentPage, limit, mock) => 
            (selectedOrganization) ? await ProjectService.fetchAllFromOrganization(
                selectedOrganization.organization.uuid, currentPage, limit
            ) 
            : mock,

        paginationSourceFn: async (limit, mock) =>
             (selectedOrganization) ? await ProjectService.paginationOfAllFromOrganization(
                selectedOrganization.organization.uuid, 
                limit
            ) : mock,

        itemsPerPage: ITEMS_PER_PAGE
    })
    

    useEffect(() => {
        selectedOrganizationProvider.selectedOrganizationProvider()
        .then(o => setSelectedOrganization(o))
    }, [])
    

    const breadcrumbs: ItemType[] = useMemo(() => {
        if (!selectedOrganization) {
            return []
        }
        
        return [{ title: selectedOrganization.organization.presentationName, key: `/o/${selectedOrganization.organization.profileName}` },]
    }, [selectedOrganization])

    // Render loading if organization is undefined
    if (!selectedOrganization) {
        return <LoadingOutlined />
    }

    return (
        <>
            <NavigationBar title="Projetos" breadcrumbs={breadcrumbs} />
            <ReadContainer>
                <List
                    itemLayout="horizontal"
                    dataSource={projectsQuery.data}
                    renderItem={(p) => (
                        <Link to={`/o/${selectedOrganization.organization.profileName}/p/${p.profileName}`}>
                            <List.Item>
                                <List.Item.Meta 
                                    avatar={<Avatar>{p.name.charAt(0)}</Avatar>}
                                    title={p.name}
                                    description={p.shortDescription}
                                />
                            </List.Item>
                        </Link>
                    )}
                />
            </ReadContainer>
        </>
    )
}